/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import dev.rollczi.liteskull.api.SkullData;
import dev.rollczi.liteskull.api.extractor.SkullDatabase;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

class SkullCacheDatabase implements SkullDatabase {

    private final Map<String, SkullDataWithExpire> skullDataCache = new ConcurrentHashMap<>();
    private final Supplier<Instant> nowSupplier;

    SkullCacheDatabase(Supplier<Instant> nowSupplier) {
        this.nowSupplier = nowSupplier;
    }

    SkullCacheDatabase() {
        nowSupplier = Instant::now;
    }

    @Override
    public CompletableFuture<Optional<SkullData>> extractData(String playerName) {
        SkullDataWithExpire skullDataExpire = this.skullDataCache.get(playerName);

        if (skullDataExpire == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        if (skullDataExpire.expire.isBefore(nowSupplier.get())) {
            this.skullDataCache.remove(playerName);

            return CompletableFuture.completedFuture(Optional.empty());
        }

        return CompletableFuture.completedFuture(Optional.of(skullDataExpire.skullData));
    }

    @Override
    public void saveSkullData(String playerName, SkullData skullData, Instant expire) {
        this.skullDataCache.put(playerName, new SkullDataWithExpire(skullData, expire));
        this.updateCache();
    }

    private void updateCache() {
        for (Map.Entry<String, SkullDataWithExpire> entry : new HashSet<>(this.skullDataCache.entrySet())) {
            if (entry.getValue().expire.isAfter(nowSupplier.get())) {
                continue;
            }

            this.skullDataCache.remove(entry.getKey());
        }
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


