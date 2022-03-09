/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.api.extractor;

import dev.rollczi.liteskull.api.SkullData;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SkullDataDefault extends SkullExtractor {

    SkullData defaultSkullData();

    @Override
    default CompletableFuture<Optional<SkullData>> extractData(String player) {
        return CompletableFuture.completedFuture(Optional.of(this.defaultSkullData()));
    }

}
