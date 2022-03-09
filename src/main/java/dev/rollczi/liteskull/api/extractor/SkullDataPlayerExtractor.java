/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.api.extractor;

import dev.rollczi.liteskull.api.SkullData;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Class used to extract skull data from online player.
 */

public interface SkullDataPlayerExtractor extends SkullExtractor {

    /**
     * Extract data from by playerName.
     *
     * @param playerName Player name.
     * @return CompletableFuture which will be completed with an Optional SkullData.
     */
    @Override
    CompletableFuture<Optional<SkullData>> extractData(String playerName);

}
