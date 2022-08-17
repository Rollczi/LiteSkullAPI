/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.test;

import dev.rollczi.liteskull.api.PlayerIdentification;
import dev.rollczi.liteskull.api.SkullData;
import dev.rollczi.liteskull.api.extractor.SkullDataAPIExtractor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TestSkullDataAPIExtractor implements SkullDataAPIExtractor {

    @Override
    public CompletableFuture<Optional<SkullData>> extractData(PlayerIdentification playerIdentification) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public void setExecutor(Executor executor) {
    }

}
