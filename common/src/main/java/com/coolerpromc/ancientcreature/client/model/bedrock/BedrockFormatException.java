package com.coolerpromc.ancientcreature.client.model.bedrock;

/** A Bedrock resource file that is structurally unusable. The message carries the JSON path. */
public final class BedrockFormatException extends Exception {
    public BedrockFormatException(String message) {
        super(message);
    }
}
