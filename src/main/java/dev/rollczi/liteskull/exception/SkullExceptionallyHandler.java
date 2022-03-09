/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.exception;

import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkullExceptionallyHandler<T> implements Function<Throwable, T> {

    private final static Logger LOGGER = Logger.getLogger("LiteSkullAPI");

    @Override
    public T apply(Throwable throwable) {
        SkullAPIException skullAPIException = new SkullAPIException(throwable.getCause());
        Thread thread = Thread.currentThread();
        String name = thread.getName();
        long id = thread.getId();

        LOGGER.log(Level.SEVERE, "[LiteSkullAPI] Task #" + id + " " + name + " generated an exception", skullAPIException);

        throw skullAPIException;
    }

}
