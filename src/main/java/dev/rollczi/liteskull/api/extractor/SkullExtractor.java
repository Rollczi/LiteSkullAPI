/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.api.extractor;

import dev.rollczi.liteskull.api.SkullData;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Class used to extract skull data by playerName.
 */

public interface SkullExtractor {

    /**
     * Extract data from by playerName.
     *
     * @param playerName Player name.
     * @return CompletableFuture which will be completed with an Optional SkullData.
     */
    CompletableFuture<Optional<SkullData>> extractData(String playerName);

}
