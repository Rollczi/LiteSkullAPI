/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.api.extractor;

import dev.rollczi.liteskull.api.PlayerIdentification;
import dev.rollczi.liteskull.api.SkullData;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SkullDatabase extends SkullExtractor {

    @Override
    CompletableFuture<Optional<SkullData>> extractData(PlayerIdentification playerIdentification);

    void saveSkullData(PlayerIdentification identification, SkullData skullData, Instant expire);

}
