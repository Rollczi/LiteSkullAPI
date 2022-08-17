/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.api.extractor;

import dev.rollczi.liteskull.api.PlayerIdentification;
import dev.rollczi.liteskull.api.SkullData;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Class used to extract skull data by playerName.
 */

public interface SkullExtractor {

    CompletableFuture<Optional<SkullData>> extractData(PlayerIdentification playerIdentification);

}
