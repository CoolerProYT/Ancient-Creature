package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockFormatException;
import com.coolerpromc.ancientcreature.molang.MolangContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * The keyframes of one bone channel (rotation, position or scale), sampled the way Blockbench's
 * animator does so playback in game matches its preview:
 * <ul>
 *   <li>exactly on a keyframe, or before the first one, the keyframe's {@code pre} value;</li>
 *   <li>after the last keyframe, its {@code post} value;</li>
 *   <li>after a {@code step} keyframe, that keyframe's value held until the next;</li>
 *   <li>between two linear keyframes, linear interpolation from {@code post} to the next {@code pre};</li>
 *   <li>if either neighbour is {@code catmullrom}, a uniform Catmull-Rom spline through the neighbours,
 *       skipping the outer neighbour when the inner keyframe has a split pre/post value.</li>
 * </ul>
 */
public record BedrockAnimationChannel(List<BedrockKeyframe> keyframes) {
    public static final BedrockAnimationChannel EMPTY = new BedrockAnimationChannel(List.of());
    private static final float EPSILON = 1.0F / 1200.0F;

    public boolean isEmpty() {
        return this.keyframes.isEmpty();
    }

    public float lastTime() {
        return this.keyframes.isEmpty() ? 0.0F : this.keyframes.getLast().time();
    }

    static BedrockAnimationChannel parse(JsonElement element, boolean scale, String path) throws BedrockFormatException {
        if (element == null) {
            return EMPTY;
        }
        List<BedrockKeyframe> frames = new ArrayList<>();
        if (element.isJsonObject() && !isKeyframeObject(element.getAsJsonObject())) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                float time;
                try {
                    time = Float.parseFloat(entry.getKey().trim());
                } catch (NumberFormatException e) {
                    throw new BedrockFormatException(path + " has a non-numeric keyframe time '" + entry.getKey() + "'");
                }
                frames.add(BedrockKeyframe.parse(time, entry.getValue(), scale, path + "." + entry.getKey()));
            }
        } else {
            frames.add(BedrockKeyframe.parse(0.0F, element, scale, path));
        }
        frames.sort(Comparator.comparingDouble(BedrockKeyframe::time));
        return new BedrockAnimationChannel(List.copyOf(frames));
    }

    private static boolean isKeyframeObject(JsonObject object) {
        return object.has("pre") || object.has("post") || object.has("lerp_mode");
    }

    /** Samples the channel at {@code time} seconds into the animation. */
    public Vector3f sample(float time, MolangContext ctx, Vector3f out) {
        int count = this.keyframes.size();
        if (count == 0) {
            return out.set(0.0F);
        }
        if (count == 1) {
            BedrockKeyframe only = this.keyframes.getFirst();
            if (time <= only.time() + EPSILON) {
                only.pre(ctx, out);
            } else {
                only.post(ctx, out);
            }
            return out;
        }

        // before: last keyframe strictly earlier than time; after: first at or later than time
        int afterIndex = this.firstAtOrAfter(time);
        int beforeIndex = afterIndex - 1;

        if (beforeIndex >= 0 && Math.abs(this.keyframes.get(beforeIndex).time() - time) <= EPSILON) {
            this.keyframes.get(beforeIndex).pre(ctx, out);
            return out;
        }
        if (afterIndex < count && Math.abs(this.keyframes.get(afterIndex).time() - time) <= EPSILON) {
            this.keyframes.get(afterIndex).pre(ctx, out);
            return out;
        }
        if (beforeIndex < 0) {
            this.keyframes.get(afterIndex).pre(ctx, out);
            return out;
        }
        BedrockKeyframe before = this.keyframes.get(beforeIndex);
        if (afterIndex >= count || before.interpolation() == BedrockKeyframe.Interpolation.STEP) {
            before.post(ctx, out);
            return out;
        }
        BedrockKeyframe after = this.keyframes.get(afterIndex);
        float span = after.time() - before.time();
        float alpha = span <= 1.0E-6F ? 0.0F : (time - before.time()) / span;

        if (before.interpolation() == BedrockKeyframe.Interpolation.CATMULL_ROM || after.interpolation() == BedrockKeyframe.Interpolation.CATMULL_ROM) {
            Vector3f p1 = new Vector3f();
            Vector3f p2 = new Vector3f();
            before.post(ctx, p1);
            after.pre(ctx, p2);
            Vector3f p0 = new Vector3f(p1);
            Vector3f p3 = new Vector3f(p2);
            if (beforeIndex > 0 && !before.hasSplitValue()) {
                this.keyframes.get(beforeIndex - 1).post(ctx, p0);
            }
            if (afterIndex + 1 < count && !after.hasSplitValue()) {
                this.keyframes.get(afterIndex + 1).pre(ctx, p3);
            }
            return out.set(
                catmullRom(p0.x, p1.x, p2.x, p3.x, alpha),
                catmullRom(p0.y, p1.y, p2.y, p3.y, alpha),
                catmullRom(p0.z, p1.z, p2.z, p3.z, alpha));
        }

        Vector3f a = new Vector3f();
        before.post(ctx, a);
        after.pre(ctx, out);
        return out.set(a.x + (out.x - a.x) * alpha, a.y + (out.y - a.y) * alpha, a.z + (out.z - a.z) * alpha);
    }

    private int firstAtOrAfter(float time) {
        int low = 0;
        int high = this.keyframes.size();
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (this.keyframes.get(mid).time() < time) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }

    private static float catmullRom(float p0, float p1, float p2, float p3, float t) {
        float t2 = t * t;
        float t3 = t2 * t;
        return 0.5F * ((2.0F * p1) + (-p0 + p2) * t + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * t2 + (-p0 + 3.0F * p1 - 3.0F * p2 + p3) * t3);
    }
}
