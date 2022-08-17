/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.extractor;

import dev.rollczi.liteskullapi.PlayerIdentification;
import dev.rollczi.liteskullapi.SkullData;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SkullDatabase extends SkullExtractor {

    @Override
    CompletableFuture<Optional<SkullData>> extractData(PlayerIdentification playerIdentification);

    void saveSkullData(PlayerIdentification identification, SkullData skullData, Instant expire);

}
