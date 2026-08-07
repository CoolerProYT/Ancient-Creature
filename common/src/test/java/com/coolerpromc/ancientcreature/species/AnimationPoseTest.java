package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.client.animation.bedrock.BedrockAnimation;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometry;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockModelBaker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Locks down the direction each clip actually poses a creature in.
 *
 * <p>Minecraft's convention is that a <em>positive</em> {@code xRot} on a head-like part pitches its
 * front end downward — vanilla sets {@code head.xRot} straight from the entity's pitch, where positive
 * is looking down. A jaw hangs below its pivot and extends forward, so it must swing positive to open
 * and negative closes it into the skull.
 *
 * <p>Bedrock animation keyframes are authored with the opposite X handedness to Bedrock geometry bone
 * rotations, so the applier negates X for the rest pose but must not for keyframes. Getting that
 * backwards is invisible in a diff and silently inverts every clip: heads pitch down where they should
 * rise, and mouths clamp shut where they should gape. These assertions are the guard.
 */
class AnimationPoseTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest(name = "{0} {1} @{2}s raises the head and opens the jaw")
    @CsvSource({
        // species, animation, time at which the pose is at its most open
        "tyrannosaurus_rex, roar,   0.62",
        "triceratops,       bellow, 1.10",
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
        // 0.18s is the lunge; by 0.34s the clip has already snapped the head back up on the recoil.
        Pose lunge = poseAt("tyrannosaurus_rex", "bite", 0.18F);
        assertTrue(lunge.head > 0.0F,
            () -> "tyrannosaurus_rex bite: head xRot is " + degrees(lunge.head) + ", but the lunge drives it down.");

        Pose widest = poseAt("tyrannosaurus_rex", "bite", 0.31F);
        assertTrue(widest.jaw > 0.0F,
            () -> "tyrannosaurus_rex bite: jaw xRot is " + degrees(widest.jaw) + ", but a bite must open the mouth.");
    }

    @Test
    void everyShippedClipResolvesAllOfItsBones() throws IOException {
        for (String species : new String[]{"tyrannosaurus_rex", "triceratops", "megalodon"}) {
            Map<String, BedrockAnimation> animations = loadAnimations(species);
            for (Map.Entry<String, BedrockAnimation> entry : animations.entrySet()) {
                ModelPart root = BedrockModelBaker.bake(loadGeometry(species));
                Set<String> missing = new HashSet<>();
                entry.getValue().withName(entry.getKey()).apply(root, 0.1F, 1.0F, missing);
                assertTrue(missing.isEmpty(),
                    () -> entry.getKey() + " targets bones the geometry does not have: " + missing);
            }
        }
    }

    private record Pose(float head, float jaw) {
    }

    private static Pose poseAt(String species, String anim, float time) throws IOException {
        String key = "animation." + species + "." + anim;
        BedrockAnimation animation = loadAnimations(species).get(key);
        assertTrue(animation != null, () -> "no animation named " + key);

        ModelPart root = BedrockModelBaker.bake(loadGeometry(species));
        animation.withName(key).apply(root, time, 1.0F, new HashSet<>());
        return new Pose(find(root, "head").xRot, find(root, "jaw").xRot);
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
            return BedrockAnimation.FILE_CODEC.parse(JsonOps.INSTANCE, json)
                .getOrThrow(message -> new AssertionError(path + ": " + message));
        }
    }

    private static BedrockGeometry loadGeometry(String species) throws IOException {
        Path path = Path.of("src/main/resources/assets/ancientcreature/ancientcreature/geo/" + species + ".geo.json");
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement json = new Gson().fromJson(reader, JsonElement.class);
            return BedrockGeometry.FILE_CODEC.parse(JsonOps.INSTANCE, json)
                .getOrThrow(message -> new AssertionError(path + ": " + message)).getFirst();
        }
    }
}
