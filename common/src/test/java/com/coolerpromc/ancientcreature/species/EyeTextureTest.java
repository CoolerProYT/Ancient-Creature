package com.coolerpromc.ancientcreature.species;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EyeTextureTest {
    private static final Path TEXTURES = Path.of("src/main/resources/assets/ancientcreature/textures/entity");

    @Test
    void brachiosaurusEyeUvIsOpaqueAndContrasting() throws IOException {
        BufferedImage texture = load("brachiosaurus");
        int iris = opaqueRegion(texture, 4, 180, 8, 6, "Brachiosaurus iris");
        int pupil = opaqueRegion(texture, 44, 180, 5, 3, "Brachiosaurus pupil");

        assertNotEquals(iris, pupil, "Brachiosaurus iris and pupil must contrast");
    }

    @Test
    void deinonychusEyeUvIsOpaqueAndContrasting() throws IOException {
        BufferedImage texture = load("deinonychus");
        int socket = opaqueRegion(texture, 4, 220, 13, 11, "Deinonychus socket");
        int iris = opaqueRegion(texture, 36, 220, 9, 8, "Deinonychus iris");
        int pupil = opaqueRegion(texture, 60, 220, 6, 5, "Deinonychus pupil");

        assertNotEquals(socket, iris, "Deinonychus socket and iris must contrast");
        assertNotEquals(iris, pupil, "Deinonychus iris and pupil must contrast");
    }

    private static BufferedImage load(String species) throws IOException {
        return ImageIO.read(TEXTURES.resolve(species + ".png").toFile());
    }

    private static int opaqueRegion(BufferedImage texture, int x, int y, int width, int height, String label) {
        int first = texture.getRGB(x, y);
        for (int pixelY = y; pixelY < y + height; pixelY++) {
            for (int pixelX = x; pixelX < x + width; pixelX++) {
                int argb = texture.getRGB(pixelX, pixelY);
                assertTrue((argb >>> 24) != 0,
                    label + " UV contains a transparent pixel at " + pixelX + "," + pixelY);
            }
        }
        return first;
    }
}
