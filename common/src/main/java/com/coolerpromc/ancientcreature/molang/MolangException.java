package com.coolerpromc.ancientcreature.molang;

/** A Molang source string that could not be compiled. Thrown at load time, never while evaluating. */
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
