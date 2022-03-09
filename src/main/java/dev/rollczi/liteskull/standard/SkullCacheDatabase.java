/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import dev.rollczi.liteskull.api.SkullData;
import dev.rollczi.liteskull.api.extractor.SkullDatabase;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SkullCacheDatabase implements SkullDatabase {

    private final Map<String, SkullDataWithExpire> skullDataCache = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Optional<SkullData>> extractData(String playerName) {
        Optional<SkullData> skullData = Optional.of(this.skullDataCache.get(playerName))
                .filter(skullDataExpire -> skullDataExpire.expire.isBefore(Instant.now()))
                .map(skullDataWithExpire -> skullDataWithExpire.skullData);

        return CompletableFuture.completedFuture(skullData);
    }

    @Override
    public void saveSkullData(String playerName, SkullData skullData, Instant expire) {
        this.skullDataCache.put(playerName, new SkullDataWithExpire(skullData, expire));
    }

    private static class SkullDataWithExpire {
        private final SkullData skullData;
        private final Instant expire;

        private SkullDataWithExpire(SkullData skullData, Instant expire) {
            this.skullData = skullData;
            this.expire = expire;
        }
    }

}


