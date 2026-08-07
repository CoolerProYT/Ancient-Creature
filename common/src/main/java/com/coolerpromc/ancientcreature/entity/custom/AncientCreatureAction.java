package com.coolerpromc.ancientcreature.entity.custom;

/**
 * A discrete, server-authoritative action an {@link AncientCreatureEntity} is performing.
 *
 * <p>Synchronised as a single byte so the client's animation controller can pick the matching clip.
 * Continuous state (moving, sprinting, in water, baby, health) is <em>not</em> here — the client can
 * derive that safely on its own. Only actions that the client cannot infer get a slot.
 *
 * <p>The client never sets these. It reads them and animates; the server decides when they start and
 * stop, and remains the only side that applies damage.
 */
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
