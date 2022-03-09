/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.rollczi.liteskull.api.SkullAPI;
import dev.rollczi.liteskull.api.SkullCreator;
import dev.rollczi.liteskull.api.SynchronizedExecutor;
import dev.rollczi.liteskull.api.extractor.SkullDataDefault;
import dev.rollczi.liteskull.api.extractor.SkullDataAPIExtractor;
import dev.rollczi.liteskull.api.extractor.SkullDataPlayerExtractor;
import dev.rollczi.liteskull.api.SkullData;
import dev.rollczi.liteskull.api.extractor.SkullDatabase;
import dev.rollczi.liteskull.shared.CompletableHandler;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class LiteSkullAPI implements SkullAPI {

    private final Cache<String, SkullData> cachedSkulls;

    private final SkullDataPlayerExtractor playerExtractor;
    private final SkullDatabase database;
    private final Duration dataBaseSaveExpire;
    private final SkullDataAPIExtractor apiExtractor;
    private final SkullDataDefault skullDataDefault;
    private final SkullCreator creator;
    private final SynchronizedExecutor syncExecutor;
    private final CompletableHandler completableHandler;

    public LiteSkullAPI(
            SkullDataPlayerExtractor playerExtractor,
            SkullDatabase database,
            Duration dataBaseSaveExpire,
            SkullDataAPIExtractor apiExtractor,
            SkullDataDefault skullDataDefault,
            SkullCreator creator,
            Duration expireAfterWrite,
            Duration expireAfterAccess,
            SynchronizedExecutor syncExecutor,
            Executor executor
    ) {
        this.playerExtractor = playerExtractor;
        this.database = database;
        this.dataBaseSaveExpire = dataBaseSaveExpire;
        this.apiExtractor = apiExtractor;
        this.skullDataDefault = skullDataDefault;
        this.creator = creator;
        this.syncExecutor = syncExecutor;
        this.completableHandler = new CompletableHandler(executor);

        CacheBuilder<Object, Object> cache = CacheBuilder.newBuilder();

        if (expireAfterWrite != Duration.ZERO) {
            cache.expireAfterWrite(expireAfterWrite.get(ChronoUnit.SECONDS), TimeUnit.SECONDS);
        }

        if (expireAfterAccess != Duration.ZERO) {
            cache.expireAfterAccess(expireAfterAccess.get(ChronoUnit.SECONDS), TimeUnit.SECONDS);
        }

        this.cachedSkulls = cache.build();
    }

    @Override
    public @NotNull CompletableFuture<ItemStack> getSkull(String playerName) {
        CompletableFuture<ItemStack> completableItem = new CompletableFuture<>();

        this.completableHandler.of(this.getSkullData(playerName)).acceptAsync(skullData -> completableItem.complete(creator.create(skullData)));
        return completableItem;
    }

    @Override
    public @NotNull CompletableFuture<SkullData> getSkullData(String playerName) {
        CompletableFuture<SkullData> completableSkullData = new CompletableFuture<>();
        CompletableFuture<SkullData> completableSkullDataToCache = new CompletableFuture<>();
        CompletableFuture<SkullData> completableSkullDataToDataBase = new CompletableFuture<>();

        this.completableHandler.of(completableSkullDataToCache).acceptAsync(skullData -> this.cachedSkulls.put(playerName, skullData));
        this.completableHandler.of(completableSkullDataToDataBase).acceptAsync(skullData -> {
            Instant expire = Instant.now().plus(this.dataBaseSaveExpire);

            this.database.saveSkullData(playerName, skullData, expire);
        });

        SkullData cachedSkull = cachedSkulls.getIfPresent(playerName);

        if (cachedSkull != null) {
            return CompletableFuture.completedFuture(cachedSkull);
        }

        // [Async] Get from Player
        this.completableHandler.of(playerExtractor.extractData(playerName)).acceptAsync(optional -> {
            if (optional.isPresent()) {
                Bukkit.broadcastMessage("From profile " + playerName);
                completableSkullData.complete(optional.get());
                completableSkullDataToCache.complete(optional.get());
                completableSkullDataToDataBase.complete(optional.get());
                return;
            }

            // [Async] Get from Database
            this.completableHandler.of(database.extractData(playerName)).acceptAsync(optionalData -> {
                if (optionalData.isPresent()) {
                    Bukkit.broadcastMessage("From Database " + playerName);
                    completableSkullData.complete(optionalData.get());
                    completableSkullDataToCache.complete(optionalData.get());
                    return;
                }

                // [Async] Get from API
                this.completableHandler.of(apiExtractor.extractData(playerName)).acceptAsync(optionalApi -> {
                    if (optionalApi.isPresent()) {
                        Bukkit.broadcastMessage("From API " + playerName);
                        completableSkullData.complete(optionalApi.get());
                        completableSkullDataToCache.complete(optionalApi.get());
                        completableSkullDataToDataBase.complete(optionalApi.get());
                        return;
                    }


                    // Get default skull data
                    Bukkit.broadcastMessage("From default " + playerName);
                    completableSkullData.complete(skullDataDefault.defaultSkullData());
                });
            });
        });

        return completableSkullData;
    }

    @NotNull
    @Blocking
    @Override
    public ItemStack awaitForSkull(String playerName, long timeout, TimeUnit unit) {
        try {
            return this.getSkull(playerName).get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            return this.creator.create(skullDataDefault.defaultSkullData());
        }
    }

    @NotNull
    @Blocking
    @Override
    public SkullData awaitForSkullData(String playerName, long timeout, TimeUnit unit) {
        try {
            return this.getSkullData(playerName).get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            return skullDataDefault.defaultSkullData();
        }
    }

    @Override
    public void acceptSyncSkull(String playerName, Consumer<ItemStack> skullConsumer) {
        this.completableHandler.of(this.getSkull(playerName))
                .acceptAsync(itemStack -> this.syncExecutor.execute(() -> skullConsumer.accept(itemStack)));
    }

    @Override
    public void acceptAsyncSkull(String playerName, Consumer<ItemStack> skullConsumer) {
        this.completableHandler.of(this.getSkull(playerName))
                .acceptAsync(skullConsumer);
    }

    @Override
    public void acceptSyncSkullData(String playerName, Consumer<SkullData> skullConsumer) {
        this.completableHandler.of(this.getSkullData(playerName))
                .acceptAsync(skullData -> this.syncExecutor.execute(() -> skullConsumer.accept(skullData)));
    }

    @Override
    public void acceptAsyncSkullData(String playerName, Consumer<SkullData> skullConsumer) {
        this.completableHandler.of(this.getSkullData(playerName))
                .acceptAsync(skullConsumer);
    }

}
