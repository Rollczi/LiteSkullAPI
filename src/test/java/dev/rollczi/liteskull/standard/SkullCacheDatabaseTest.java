/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import dev.rollczi.liteskull.api.SkullData;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SkullCacheDatabaseTest {

    private final static SkullData DATA = new SkullData("", "");

    private final static Instant PAST = Instant.ofEpochSecond(0);
    private final static Instant NOW = Instant.ofEpochSecond(100);
    private final static Instant FUTURE = Instant.ofEpochSecond(1000);

    @Test
    public void test() {
        SkullCacheDatabase database = new SkullCacheDatabase(() -> NOW);

        database.saveSkullData("before-expire", DATA, FUTURE);
        database.saveSkullData("expired", DATA, PAST);

        assertTrue(isPresent(database.extractData("before-expire")));
        assertFalse(isPresent(database.extractData("expired")));
    }

    private <T> boolean isPresent(CompletableFuture<Optional<T>> future) {
        return future.getNow(Optional.empty()).isPresent();
    }

}
