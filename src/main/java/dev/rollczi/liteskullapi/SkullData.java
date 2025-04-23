/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi;

public class SkullData {

    private final String name;
    private final String signature;
    private final String texture;

    public SkullData(String name, String signature, String texture) {
        this.name = name;
        this.signature = signature;
        this.texture = texture;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    @Deprecated
    public String getValue() {
        return texture;
    }

    public String getTexture() {
        return texture;
    }

}
