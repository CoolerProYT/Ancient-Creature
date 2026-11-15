package com.coolerpromc.ancientcreature.entity.custom;

public enum AncientCreatureAction {
    NONE,
    ATTACK,
    ROAR,
    GRAZE,
    EAT;

    private static final AncientCreatureAction[] VALUES = values();

    public byte id() {
        return (byte) this.ordinal();
    }

    public static AncientCreatureAction byId(byte id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : NONE;
    }

    public String queryName() {
        return this.name().toLowerCase(java.util.Locale.ROOT);
    }
}
