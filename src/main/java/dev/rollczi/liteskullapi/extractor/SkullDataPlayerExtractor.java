/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.extractor;

import dev.rollczi.liteskullapi.PlayerIdentification;
import dev.rollczi.liteskullapi.SkullData;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Class used to extract skull data from online player.
 */

public interface SkullDataPlayerExtractor extends SkullExtractor {

    @Override
    CompletableFuture<Optional<SkullData>> extractData(PlayerIdentification playerIdentification);

    void setExecutor(Executor executor);

}
