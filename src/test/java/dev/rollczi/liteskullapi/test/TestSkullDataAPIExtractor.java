/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.test;

import dev.rollczi.liteskullapi.PlayerIdentification;
import dev.rollczi.liteskullapi.SkullData;
import dev.rollczi.liteskullapi.extractor.SkullDataAPIExtractor;

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
