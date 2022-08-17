/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import dev.rollczi.liteskull.api.PlayerIdentification;
import dev.rollczi.liteskull.api.SkullData;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkullCacheDatabaseTest {

    private final static SkullData DATA = new SkullData("", "");

    private final static Instant PAST = Instant.ofEpochSecond(0);
    private final static Instant NOW = Instant.ofEpochSecond(100);
    private final static Instant FUTURE = Instant.ofEpochSecond(1000);

    private final static PlayerIdentification BEFORE_ID = PlayerIdentification.of("before-expire");
    private final static PlayerIdentification EXPIRED_ID = PlayerIdentification.of("expired");

    @Test
    void test() {
        SkullCacheDatabase database = new SkullCacheDatabase(() -> NOW);

        database.saveSkullData(BEFORE_ID, DATA, FUTURE);
        database.saveSkullData(EXPIRED_ID, DATA, PAST);

        assertTrue(isPresent(database.extractData(BEFORE_ID)));
        assertFalse(isPresent(database.extractData(EXPIRED_ID)));
    }

    private <T> boolean isPresent(CompletableFuture<Optional<T>> future) {
        return future.getNow(Optional.empty()).isPresent();
    }

}
