package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockFormatException;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockJson;
import com.coolerpromc.ancientcreature.molang.Molang;
import com.coolerpromc.ancientcreature.molang.MolangContext;
import com.coolerpromc.ancientcreature.molang.MolangExpression;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector3f;

import java.util.Locale;

/**
 * One keyframe of a bone channel. Each axis is a Molang expression — usually a folded constant.
 *
 * <p>{@code pre} is the value arriving at the keyframe and {@code post} the value leaving it; they
 * differ only for keyframes Blockbench exports with a discontinuity (and for its step keyframes).
 */
public record BedrockKeyframe(float time, MolangExpression[] pre, MolangExpression[] post, Interpolation interpolation) {
    public enum Interpolation {
        LINEAR, CATMULL_ROM, STEP;

        static Interpolation parse(String name) {
            return switch (name.toLowerCase(Locale.ROOT)) {
                case "catmullrom", "catmull_rom", "smooth" -> CATMULL_ROM;
                case "step" -> STEP;
                default -> LINEAR;
            };
        }
    }

    public boolean hasSplitValue() {
        return this.pre != this.post;
    }

    public void pre(MolangContext ctx, Vector3f out) {
        out.set(this.pre[0].evaluate(ctx), this.pre[1].evaluate(ctx), this.pre[2].evaluate(ctx));
    }

    public void post(MolangContext ctx, Vector3f out) {
        out.set(this.post[0].evaluate(ctx), this.post[1].evaluate(ctx), this.post[2].evaluate(ctx));
    }

    public boolean isConstant() {
        for (int i = 0; i < 3; i++) {
            if (!this.pre[i].isConstant() || !this.post[i].isConstant()) {
                return false;
            }
        }
        return true;
    }

    /** A keyframe value: a number, a Molang string, a 1- or 3-element array, or {pre, post, lerp_mode}. */
    static BedrockKeyframe parse(float time, JsonElement element, boolean uniformDefault, String path) throws BedrockFormatException {
        if (element.isJsonObject()) {
            JsonObject json = element.getAsJsonObject();
            JsonElement preJson = BedrockJson.get(json, "pre");
            JsonElement postJson = BedrockJson.get(json, "post");
            if (preJson == null && postJson == null) {
                throw new BedrockFormatException(path + " needs 'pre' and/or 'post'");
            }
            MolangExpression[] post = postJson != null ? vector(postJson, uniformDefault, path + ".post") : null;
            MolangExpression[] pre = preJson != null ? vector(preJson, uniformDefault, path + ".pre") : post;
            if (post == null) {
                post = pre;
            }
            Interpolation lerp = Interpolation.parse(BedrockJson.string(json, "lerp_mode", "linear"));
            return new BedrockKeyframe(time, pre, post, lerp);
        }
        MolangExpression[] value = vector(element, uniformDefault, path);
        return new BedrockKeyframe(time, value, value, Interpolation.LINEAR);
    }

    /**
     * Three axis expressions. A lone value applies to all axes for scale (Bedrock's uniform scale) and
     * also for rotation and position, which is what Bedrock does with a scalar.
     */
    static MolangExpression[] vector(JsonElement element, boolean uniformDefault, String path) throws BedrockFormatException {
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            if (array.size() == 1) {
                MolangExpression e = axis(array.get(0), path + "[0]");
                return new MolangExpression[]{e, e, e};
            }
            if (array.size() != 3) {
                throw new BedrockFormatException(path + " must have 1 or 3 components, got " + array.size());
            }
            return new MolangExpression[]{axis(array.get(0), path + "[0]"), axis(array.get(1), path + "[1]"), axis(array.get(2), path + "[2]")};
        }
        if (element.isJsonPrimitive()) {
            MolangExpression e = axis(element, path);
            return new MolangExpression[]{e, e, e};
        }
        throw new BedrockFormatException(path + " must be a number, a Molang string or an array");
    }

    private static MolangExpression axis(JsonElement element, String path) throws BedrockFormatException {
        if (!element.isJsonPrimitive()) {
            throw new BedrockFormatException(path + " must be a number or a Molang string");
        }
        return Molang.of(BedrockJson.molangSource(element), path);
    }
}
