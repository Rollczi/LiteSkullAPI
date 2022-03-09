/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.api.extractor;

import dev.rollczi.liteskull.api.SkullData;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SkullDatabase extends SkullExtractor {

    @Override
    CompletableFuture<Optional<SkullData>> extractData(String playerName);

    void saveSkullData(String playerName, SkullData skullData, Instant expire);

}
