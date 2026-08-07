package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.Constants;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Map;

public record BedrockAnimationChannel(List<BedrockKeyframe> keyframes) {
    public static final BedrockAnimationChannel EMPTY = new BedrockAnimationChannel(List.of());

    public static final Codec<BedrockAnimationChannel> CODEC = Codec.either(
        Codec.unboundedMap(Codec.STRING, BedrockKeyframe.VALUE_CODEC),
        BedrockKeyframe.VALUE_CODEC
    ).xmap(
        either -> either.map(BedrockAnimationChannel::fromMap,
            constant -> new BedrockAnimationChannel(List.of(BedrockKeyframe.of(0.0F, constant)))),
        channel -> Either.left(channel.toMap())
    );

    private static BedrockAnimationChannel fromMap(Map<String, BedrockKeyframe.ValueAtTime> map) {
        List<BedrockKeyframe> frames = map.entrySet().stream()
            .map(entry -> {
                float time;
                try {
                    time = Float.parseFloat(entry.getKey());
                } catch (NumberFormatException e) {
                    Constants.LOG.warn("Ignoring animation keyframe with a non-numeric timestamp '{}'", entry.getKey());
                    return null;
                }
                return BedrockKeyframe.of(time, entry.getValue());
            })
            .filter(java.util.Objects::nonNull)
            .toList();
        return new BedrockAnimationChannel(BedrockKeyframe.sorted(frames));
    }

    private Map<String, BedrockKeyframe.ValueAtTime> toMap() {
        return this.keyframes.stream().collect(java.util.stream.Collectors.toMap(
            frame -> Float.toString(frame.time()),
            frame -> new BedrockKeyframe.ValueAtTime(frame.pre(), frame.post(), frame.interpolation()),
            (a, b) -> b,
            java.util.LinkedHashMap::new));
    }

    public boolean isEmpty() {
        return this.keyframes.isEmpty();
    }

    public Vector3f sample(float time, Vector3f out) {
        int count = this.keyframes.size();
        if (count == 0) {
            return out.set(0.0F, 0.0F, 0.0F);
        }
        if (count == 1) {
            return out.set(this.keyframes.getFirst().post());
        }

        BedrockKeyframe first = this.keyframes.getFirst();
        if (time <= first.time()) {
            return out.set(first.post());
        }
        BedrockKeyframe last = this.keyframes.getLast();
        if (time >= last.time()) {
            return out.set(last.pre());
        }

        int index = this.indexBefore(time);
        BedrockKeyframe from = this.keyframes.get(index);
        BedrockKeyframe to = this.keyframes.get(index + 1);

        float span = to.time() - from.time();
        float progress = span <= 1.0E-6F ? 0.0F : (time - from.time()) / span;

        if (from.interpolation() == BedrockKeyframe.Interpolation.CATMULL_ROM) {
            Vector3fc before = this.keyframes.get(Math.max(0, index - 1)).post();
            Vector3fc after = this.keyframes.get(Math.min(count - 1, index + 2)).pre();
            return catmullRom(before, from.post(), to.pre(), after, progress, out);
        }

        Vector3fc a = from.post();
        Vector3fc b = to.pre();
        return out.set(a.x() + (b.x() - a.x()) * progress, a.y() + (b.y() - a.y()) * progress, a.z() + (b.z() - a.z()) * progress);
    }

    private int indexBefore(float time) {
        int low = 0;
        int high = this.keyframes.size() - 1;
        while (low < high) {
            int mid = (low + high + 1) >>> 1;
            if (this.keyframes.get(mid).time() <= time) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }
        return Math.min(low, this.keyframes.size() - 2);
    }

    private static Vector3f catmullRom(Vector3fc p0, Vector3fc p1, Vector3fc p2, Vector3fc p3, float t, Vector3f out) {
        return out.set(
            catmullRom(p0.x(), p1.x(), p2.x(), p3.x(), t),
            catmullRom(p0.y(), p1.y(), p2.y(), p3.y(), t),
            catmullRom(p0.z(), p1.z(), p2.z(), p3.z(), t));
    }

    private static float catmullRom(float p0, float p1, float p2, float p3, float t) {
        float t2 = t * t;
        float t3 = t2 * t;
        return 0.5F * ((2.0F * p1) + (-p0 + p2) * t + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * t2 + (-p0 + 3.0F * p1 - 3.0F * p2 + p3) * t3);
    }
}
