package com.coolerpromc.ancientcreature.molang;

import org.jspecify.annotations.Nullable;

public interface MolangResources {
    MolangResources NONE = new MolangResources() {
    };

    default @Nullable Object[] array(String name) {
        return null;
    }

    record ResourceRef(String category, String name) {
        @Override
        public String toString() {
            return this.category + "." + this.name;
        }
    }
}
