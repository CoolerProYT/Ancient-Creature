package com.coolerpromc.ancientcreature.molang;

public interface MolangQueryHost {
    MolangQueryHost NONE = (name, args) -> Double.NaN;

    double queryNumber(String name, MolangArgs args);

    default Object queryObject(String name, MolangArgs args) {
        return this.queryNumber(name, args);
    }
}
