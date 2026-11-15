package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.client.animation.bedrock.BedrockAnimation;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometry;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockModelBaker;
import com.coolerpromc.ancientcreature.client.model.bedrock.BakedBedrockModel;
import com.coolerpromc.ancientcreature.molang.MolangContext;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.server.Bootstrap;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnimationPoseTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest(name = "{0} {1} @{2}s raises the head and opens the jaw")
    @CsvSource({

        "tyrannosaurus_rex, roar,    0.62",
        "triceratops,       bellow,  1.10",
        "pteranodon,        screech, 0.50",
    })
    void callingClipsLookUpAndOpenWide(String species, String anim, float time) throws IOException {
        Pose pose = poseAt(species, anim, time);

        assertTrue(pose.head < 0.0F,
            () -> species + " " + anim + ": head xRot is " + degrees(pose.head)
                + ", which pitches the snout down. A call should raise it (negative xRot).");
        assertTrue(pose.jaw > 0.0F,
            () -> species + " " + anim + ": jaw xRot is " + degrees(pose.jaw)
                + ", which shuts the mouth into the skull. A call should gape it open (positive xRot).");
    }

    @Test
    void grazingLowersTheHead() throws IOException {
        Pose pose = poseAt("triceratops", "graze", 1.65F);
        assertTrue(pose.head > 0.0F,
            () -> "triceratops graze: head xRot is " + degrees(pose.head)
                + ", but grazing must pitch the snout down to the ground (positive xRot).");
    }

    @Test
    void bitingLungesTheHeadDownAndOpensTheJaw() throws IOException {

        Pose lunge = poseAt("tyrannosaurus_rex", "bite", 0.18F);
        assertTrue(lunge.head > 0.0F,
            () -> "tyrannosaurus_rex bite: head xRot is " + degrees(lunge.head) + ", but the lunge drives it down.");

        Pose widest = poseAt("tyrannosaurus_rex", "bite", 0.31F);
        assertTrue(widest.jaw > 0.0F,
            () -> "tyrannosaurus_rex bite: jaw xRot is " + degrees(widest.jaw) + ", but a bite must open the mouth.");
    }

    @Test
    void everyShippedClipResolvesAllOfItsBones() throws IOException {
        for (String species : new String[]{"tyrannosaurus_rex", "triceratops", "megalodon", "pteranodon", "ankylosaurus", "deinonychus", "brachiosaurus",
            "stegosaurus", "parasaurolophus", "spinosaurus", "carnotaurus", "velociraptor", "dilophosaurus", "argentinosaurus", "quetzalcoatlus",
            "mosasaurus", "plesiosaurus", "dunkleosteus", "smilodon", "woolly_mammoth", "woolly_rhinoceros", "dire_wolf", "arthropleura","allosaurus", "pachycephalosaurus", "gallimimus", "therizinosaurus", "compsognathus", "diplodocus"}) {
            Map<String, BedrockAnimation> animations = loadAnimations(species);
            for (Map.Entry<String, BedrockAnimation> entry : animations.entrySet()) {
                BakedBedrockModel root = BedrockModelBaker.bake(loadGeometry(species));
                Set<String> missing = new HashSet<>();
                entry.getValue().apply(root, 0.1F, 1.0F, new MolangContext(), missing, new java.util.ArrayList<>());
                assertTrue(missing.isEmpty(),
                    () -> entry.getKey() + " targets bones the geometry does not have: " + missing);
                BedrockAnimation clip = entry.getValue();
                for (var bone : clip.bones().entrySet()) {
                    for (var channel : List.of(bone.getValue().rotation(), bone.getValue().position(), bone.getValue().scale())) {
                        assertTrue(channel.lastTime() <= clip.length() + 0.0001F,
                            () -> entry.getKey() + "/" + bone.getKey() + " has keyframes beyond its duration");
                        if (clip.loop() == BedrockAnimation.LoopMode.LOOP && entry.getKey().matches(".*\\.(walk|run|swim|fly)")) {
                            Vector3f first = channel.sample(0, new MolangContext(), new Vector3f());
                            Vector3f last = channel.sample(clip.length(), new MolangContext(), new Vector3f());
                            assertTrue(first.equals(last, 0.01F),
                                () -> entry.getKey() + "/" + bone.getKey() + " snaps at its loop boundary: " + first + " vs " + last);
                        }
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"argentinosaurus", "arthropleura", "carnotaurus", "dilophosaurus", "dire_wolf",
        "dunkleosteus", "mosasaurus", "parasaurolophus", "plesiosaurus", "quetzalcoatlus", "smilodon",
        "spinosaurus", "stegosaurus", "velociraptor", "woolly_mammoth", "woolly_rhinoceros", "allosaurus",
        "pachycephalosaurus", "gallimimus", "therizinosaurus", "compsognathus", "diplodocus"})
    void revisedDeathClipsFinishBeforeVanillaRemovesTheEntity(String species) throws IOException {
        BedrockAnimation death = loadAnimations(species).get("animation." + species + ".death");
        assertTrue(death.length() <= 0.8F, "Leave four ticks for the final pose before vanilla removes the body at tick 20");
        BakedBedrockModel model = BedrockModelBaker.bake(loadGeometry(species));
        Set<String> missing = new HashSet<>();
        death.apply(model, 0.8F, 1.0F, new MolangContext(), missing, new java.util.ArrayList<>());
        assertTrue(missing.isEmpty(), () -> species + ": " + missing);
    }

    @ParameterizedTest(name = "{0} mirrors the wings")
    @ValueSource(strings = {"fly", "glide", "dive", "screech", "death"})
    void flightClipsMirrorTheWings(String anim) throws IOException {
        for (float time : new float[]{0.0F, 0.15F, 0.3F, 0.5F, 0.65F}) {
            Wings wings = wingsAt("pteranodon", anim, time);
            assertEquals(-wings.left, wings.right, 1.0E-4F,
                () -> "pteranodon " + anim + " @" + time + "s: wing_left zRot is " + degrees(wings.left)
                    + " but wing_right is " + degrees(wings.right)
                    + "; the two sides must be equal and opposite.");
        }
    }

    @Test
    void theFlapRaisesThenLowersTheWings() throws IOException {
        Wings top = wingsAt("pteranodon", "fly", 0.0F);
        assertTrue(top.left > 0.0F,
            () -> "pteranodon fly starts the cycle at the top of the upstroke, but wing_left zRot is "
                + degrees(top.left) + ", which holds it below the body.");

        Wings bottom = wingsAt("pteranodon", "fly", 0.5F);
        assertTrue(bottom.left < 0.0F,
            () -> "pteranodon fly should be at the bottom of the downstroke by 0.5s, but wing_left zRot is "
                + degrees(bottom.left) + ", which still holds it up.");

        assertTrue(top.left - bottom.left > Math.toRadians(30.0),
            () -> "pteranodon fly only sweeps the wing " + degrees(top.left - bottom.left)
                + " across the stroke, which will not read as a flap.");
    }

    @Test
    void perchedIdleFoldsTheWingsBack() throws IOException {
        BakedBedrockModel root = BedrockModelBaker.bake(loadGeometry("pteranodon"));
        String key = "animation.pteranodon.idle";
        loadAnimations("pteranodon").get(key).apply(root, 1.0F, 1.0F, new MolangContext(), new HashSet<>(), new java.util.ArrayList<>());

        float left = find(root.root(), "wing_left").yRot;
        float right = find(root.root(), "wing_right").yRot;
        assertTrue(left > Math.toRadians(45.0),
            () -> "pteranodon idle only sweeps wing_left back " + degrees(left) + "; perched wings must fold in.");
        assertEquals(-left, right, 1.0E-4F,
            () -> "pteranodon idle folds wing_left " + degrees(left) + " but wing_right " + degrees(right) + ".");
    }

    private record Pose(float head, float jaw) {
    }

    private record Wings(float left, float right) {
    }

    private static Wings wingsAt(String species, String anim, float time) throws IOException {
        String key = "animation." + species + "." + anim;
        BedrockAnimation animation = loadAnimations(species).get(key);
        assertTrue(animation != null, () -> "no animation named " + key);

        BakedBedrockModel root = BedrockModelBaker.bake(loadGeometry(species));
        animation.apply(root, time, 1.0F, new MolangContext(), new HashSet<>(), new java.util.ArrayList<>());
        return new Wings(find(root.root(), "wing_left").zRot, find(root.root(), "wing_right").zRot);
    }

    private static Pose poseAt(String species, String anim, float time) throws IOException {
        String key = "animation." + species + "." + anim;
        BedrockAnimation animation = loadAnimations(species).get(key);
        assertTrue(animation != null, () -> "no animation named " + key);

        BakedBedrockModel root = BedrockModelBaker.bake(loadGeometry(species));
        animation.apply(root, time, 1.0F, new MolangContext(), new HashSet<>(), new java.util.ArrayList<>());
        return new Pose(find(root.root(), "head").xRot, find(root.root(), "jaw").xRot);
    }

    private static String degrees(float radians) {
        return String.format(java.util.Locale.ROOT, "%+.1f deg", Math.toDegrees(radians));
    }

    private static ModelPart find(ModelPart part, String name) {
        if (part.hasChild(name)) {
            return part.getChild(name);
        }
        for (ModelPart child : part.getAllParts()) {
            if (child != part && child.hasChild(name)) {
                return child.getChild(name);
            }
        }
        throw new AssertionError("no bone named " + name);
    }

    private static Map<String, BedrockAnimation> loadAnimations(String species) throws IOException {
        Path path = Path.of("src/main/resources/assets/ancientcreature/ancientcreature/animations/" + species + ".animation.json");
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement json = new Gson().fromJson(reader, JsonElement.class);
            try {
                return BedrockAnimation.parseFile(json);
            } catch (com.coolerpromc.ancientcreature.client.model.bedrock.BedrockFormatException e) {
                throw new AssertionError(path + ": " + e.getMessage());
            }
        }
    }

    private static BedrockGeometry loadGeometry(String species) throws IOException {
        Path path = Path.of("src/main/resources/assets/ancientcreature/ancientcreature/geo/" + species + ".geo.json");
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement json = new Gson().fromJson(reader, JsonElement.class);
            try {
                return BedrockGeometry.parseFile(json).getFirst();
            } catch (com.coolerpromc.ancientcreature.client.model.bedrock.BedrockFormatException e) {
                throw new AssertionError(path + ": " + e.getMessage());
            }
        }
    }
}
