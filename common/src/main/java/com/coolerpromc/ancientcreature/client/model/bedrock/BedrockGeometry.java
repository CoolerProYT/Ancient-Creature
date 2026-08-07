package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.*;

public record BedrockGeometry(String identifier, int textureWidth, int textureHeight, List<BedrockBone> bones) {
    public static final Codec<BedrockGeometry> CODEC = RecordCodecBuilder.create(i -> i.group(
        Description.CODEC.fieldOf("description").forGetter(BedrockGeometry::description),
        BedrockBone.CODEC.listOf().optionalFieldOf("bones", List.of()).forGetter(BedrockGeometry::bones)
    ).apply(i, (description, bones) -> new BedrockGeometry(description.identifier(), description.textureWidth(), description.textureHeight(), bones)));

    public static final Codec<List<BedrockGeometry>> FILE_CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("format_version", "1.12.0").forGetter(list -> "1.12.0"),
        CODEC.listOf().fieldOf("minecraft:geometry").forGetter(list -> list)
    ).apply(i, (formatVersion, list) -> list));

    private Description description() {
        return new Description(this.identifier, this.textureWidth, this.textureHeight);
    }

    public DataResult<BedrockGeometry> validate() {
        List<String> problems = new ArrayList<>();

        if (!this.identifier.startsWith("geometry.")) {
            problems.add("identifier '" + this.identifier + "' must start with 'geometry.'");
        }
        if (this.textureWidth <= 0 || this.textureHeight <= 0) {
            problems.add("texture size must be positive, got " + this.textureWidth + "x" + this.textureHeight);
        }

        Map<String, BedrockBone> byName = new LinkedHashMap<>();
        for (BedrockBone bone : this.bones) {
            if (byName.put(bone.name(), bone) != null) {
                problems.add("duplicate bone '" + bone.name() + "'");
            }
        }

        for (BedrockBone bone : this.bones) {
            bone.parent().ifPresent(parent -> {
                if (!byName.containsKey(parent)) {
                    problems.add("bone '" + bone.name() + "' has unknown parent '" + parent + "'");
                }
            });
            for (int c = 0; c < bone.cubes().size(); c++) {
                BedrockCube cube = bone.cubes().get(c);
                if (!cube.hasValidSize()) {
                    problems.add("bone '" + bone.name() + "' cube " + c + " has an invalid size "
                        + cube.size().x() + "x" + cube.size().y() + "x" + cube.size().z());
                }
                if (!Float.isFinite(cube.origin().x()) || !Float.isFinite(cube.origin().y()) || !Float.isFinite(cube.origin().z())) {
                    problems.add("bone '" + bone.name() + "' cube " + c + " has a non-finite origin");
                }
            }
            if (!Float.isFinite(bone.pivot().x()) || !Float.isFinite(bone.pivot().y()) || !Float.isFinite(bone.pivot().z())) {
                problems.add("bone '" + bone.name() + "' has a malformed pivot");
            }
        }

        problems.addAll(findCycles(byName));

        if (!problems.isEmpty()) {
            return DataResult.error(() -> String.join("; ", problems));
        }
        return DataResult.success(this);
    }

    private static List<String> findCycles(Map<String, BedrockBone> byName) {
        List<String> problems = new ArrayList<>();
        Set<String> known = new HashSet<>();

        for (BedrockBone bone : byName.values()) {
            Set<String> seen = new HashSet<>();
            Deque<String> chain = new ArrayDeque<>();
            String current = bone.name();

            while (current != null) {
                if (!seen.add(current)) {
                    String cycle = String.join(" -> ", chain) + " -> " + current;
                    if (known.add(cycle)) {
                        problems.add("cyclic bone parenting: " + cycle);
                    }
                    break;
                }
                chain.addLast(current);
                BedrockBone node = byName.get(current);
                current = node == null ? null : node.parent().orElse(null);
            }
        }
        return problems;
    }

    public Map<String, BedrockBone> boneMap() {
        Map<String, BedrockBone> map = new LinkedHashMap<>();
        for (BedrockBone bone : this.bones) {
            map.putIfAbsent(bone.name(), bone);
        }
        return map;
    }

    public Map<String, List<BedrockBone>> childrenByParent() {
        Map<String, List<BedrockBone>> map = new HashMap<>();
        for (BedrockBone bone : this.bones) {
            map.computeIfAbsent(bone.parent().orElse(""), key -> new ArrayList<>()).add(bone);
        }
        return map;
    }

    private record Description(String identifier, int textureWidth, int textureHeight) {
        private static final Codec<Description> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("identifier").forGetter(Description::identifier),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("texture_width", 64).forGetter(Description::textureWidth),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("texture_height", 64).forGetter(Description::textureHeight)
        ).apply(i, Description::new));
    }
}
