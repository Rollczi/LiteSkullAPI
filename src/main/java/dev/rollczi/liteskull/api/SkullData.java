/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.api;

public class SkullData {

    private final String signature;
    private final String value;

    public SkullData(String signature, String value) {
        this.signature = signature;
        this.value = value;
    }

    public String getSignature() {
        return signature;
    }

    public String getValue() {
        return value;
    }

}
