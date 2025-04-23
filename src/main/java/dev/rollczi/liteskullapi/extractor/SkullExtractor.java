/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.extractor;

import dev.rollczi.liteskullapi.PlayerIdentification;
import dev.rollczi.liteskullapi.SkullData;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Class used to extract skull data by playerName.
 */

public interface SkullExtractor {

    CompletableFuture<Optional<SkullData>> extractData(PlayerIdentification playerIdentification);

}
