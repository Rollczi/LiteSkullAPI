/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi;

public class SkullAPIException extends RuntimeException {

    public SkullAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public SkullAPIException(Throwable cause) {
        super(cause);
    }

}
