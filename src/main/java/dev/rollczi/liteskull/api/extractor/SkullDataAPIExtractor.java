/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.api.extractor;

import dev.rollczi.liteskull.api.SkullData;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Class used to extract skull data from Mojang or other API
 */

public interface SkullDataAPIExtractor extends SkullExtractor {

    /**
     * Async extract data from API by playerName.
     *
     * @param playerName Premium player name.
     * @return CompletableFuture which will be completed with an Optional SkullData.
     * If playerName is not Premium or API don't response return CompletableFuture with empty Optional.
     */

    @Override
    CompletableFuture<Optional<SkullData>> extractData(String playerName);

}
