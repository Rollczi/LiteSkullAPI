/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.standard;

import dev.rollczi.liteskullapi.PlayerIdentification;
import dev.rollczi.liteskullapi.SkullData;
import dev.rollczi.liteskullapi.extractor.SkullDatabase;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

class SkullCacheDatabase implements SkullDatabase {

    private final Map<String, SkullDataWithExpire> skullCacheByName = new ConcurrentHashMap<>();
    private final Map<UUID, SkullDataWithExpire> skullCacheByUuid = new ConcurrentHashMap<>();

    private final Supplier<Instant> nowSupplier;

    SkullCacheDatabase(Supplier<Instant> nowSupplier) {
        this.nowSupplier = nowSupplier;
    }

    SkullCacheDatabase() {
        nowSupplier = Instant::now;
    }

    @Override
    public CompletableFuture<Optional<SkullData>> extractData(PlayerIdentification identification) {
        SkullDataWithExpire skullDataExpire = identification.map(this.skullCacheByName::get, this.skullCacheByUuid::get);

        if (skullDataExpire == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        if (skullDataExpire.expire.isBefore(nowSupplier.get())) {
            identification.peek(this.skullCacheByName::remove, this.skullCacheByUuid::remove);

            return CompletableFuture.completedFuture(Optional.empty());
        }

        return CompletableFuture.completedFuture(Optional.of(skullDataExpire.skullData));
    }

    @Override
    public void saveSkullData(PlayerIdentification identification, SkullData skullData, Instant expire) {
        SkullDataWithExpire dataWithExpire = new SkullDataWithExpire(skullData, expire);

        identification.peek(name -> this.skullCacheByName.put(name, dataWithExpire), uuid -> this.skullCacheByUuid.put(uuid, dataWithExpire));

        this.updateCache();
    }

    private void updateCache() {
        for (Map.Entry<UUID, SkullDataWithExpire> entry : new HashSet<>(this.skullCacheByUuid.entrySet())) {
            if (entry.getValue().expire.isAfter(nowSupplier.get())) {
                continue;
            }

            this.skullCacheByUuid.remove(entry.getKey());
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


