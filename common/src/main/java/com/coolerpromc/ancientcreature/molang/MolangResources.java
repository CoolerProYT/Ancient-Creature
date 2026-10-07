package com.coolerpromc.ancientcreature.molang;

import org.jspecify.annotations.Nullable;

/**
 * Resolves the render-controller vocabulary: {@code Array.*}, {@code Geometry.*},
 * {@code Texture.*} and {@code Material.*}.
 *
 * <p>A resource reference evaluates to a {@link ResourceRef}; an array element to whatever the array
 * holds, which is normally another reference.
 */
public interface MolangResources {
    MolangResources NONE = new MolangResources() {
    };

    /** The members of {@code array.<name>}, or {@code null} when no such array is declared. */
    default @Nullable Object[] array(String name) {
        return null;
    }

    /** A reference to {@code <category>.<name>}, where category is geometry, texture or material. */
    record ResourceRef(String category, String name) {
        @Override
        public String toString() {
            return this.category + "." + this.name;
        }
    }
}
