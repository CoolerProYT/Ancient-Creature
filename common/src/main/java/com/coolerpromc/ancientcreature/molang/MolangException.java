package com.coolerpromc.ancientcreature.molang;

public final class MolangException extends Exception {
    private final String source;

    public MolangException(String message, String source) {
        super(message);
        this.source = source;
    }

    public String source() {
        return this.source;
    }
}
