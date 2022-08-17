/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.rollczi.liteskullapi.PlayerIdentification;
import dev.rollczi.liteskullapi.SkullAPI;
import dev.rollczi.liteskullapi.SkullCreator;
import dev.rollczi.liteskullapi.SynchronizedExecutor;
import dev.rollczi.liteskullapi.extractor.SkullDataDefault;
import dev.rollczi.liteskullapi.extractor.SkullDataAPIExtractor;
import dev.rollczi.liteskullapi.extractor.SkullDataPlayerExtractor;
import dev.rollczi.liteskullapi.SkullData;
import dev.rollczi.liteskullapi.extractor.SkullDatabase;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LiteSkullAPI implements SkullAPI {

    private final Cache<String, SkullData> cachedSkullsByName;
    private final Cache<UUID, SkullData> cachedSkullsByUuid;

    private final SkullDataPlayerExtractor playerExtractor;
    private final SkullDatabase database;
    private final Duration dataBaseSaveExpire;
    private final SkullDataAPIExtractor apiExtractor;
    private final SkullDataDefault skullDataDefault;
    private final SkullCreator creator;
    private final SynchronizedExecutor syncExecutor;
    private final ExecutorService asyncExecutor;

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
            ExecutorService asyncExecutor
    ) {
        this.playerExtractor = playerExtractor;
        this.database = database;
        this.dataBaseSaveExpire = dataBaseSaveExpire;
        this.apiExtractor = apiExtractor;
        this.skullDataDefault = skullDataDefault;
        this.creator = creator;
        this.syncExecutor = syncExecutor;
        this.asyncExecutor = asyncExecutor;

        this.playerExtractor.setExecutor(asyncExecutor);
        this.apiExtractor.setExecutor(asyncExecutor);

        CacheBuilder<Object, Object> cache = CacheBuilder.newBuilder();

        if (expireAfterWrite != Duration.ZERO) {
            cache.expireAfterWrite(expireAfterWrite.get(ChronoUnit.SECONDS), TimeUnit.SECONDS);
        }

        if (expireAfterAccess != Duration.ZERO) {
            cache.expireAfterAccess(expireAfterAccess.get(ChronoUnit.SECONDS), TimeUnit.SECONDS);
        }

        this.cachedSkullsByName = cache.build();
        this.cachedSkullsByUuid = cache.build();
    }

    @Override
    public @NotNull CompletableFuture<ItemStack> getSkull(String playerName) {
        return this.getSkullByIdentification(PlayerIdentification.of(playerName));
    }

    @Override
    public @NotNull CompletableFuture<ItemStack> getSkull(UUID playerUuid) {
        return this.getSkullByIdentification(PlayerIdentification.of(playerUuid));
    }

    @Override
    public @NotNull CompletableFuture<SkullData> getSkullData(String playerName) {
        return this.getSkullDataByIdentification(PlayerIdentification.of(playerName));
    }

    @Override
    public @NotNull CompletableFuture<SkullData> getSkullData(UUID playerUuid) {
        return this.getSkullDataByIdentification(PlayerIdentification.of(playerUuid));
    }

    @Blocking
    @Override
    public @NotNull ItemStack awaitSkull(String playerName, long timeout, TimeUnit unit) {
        return this.awaitSkull(PlayerIdentification.of(playerName), timeout, unit);
    }

    @Blocking
    @Override
    public @NotNull SkullData awaitSkullData(String playerName, long timeout, TimeUnit unit) {
        return this.awaitSkullData(PlayerIdentification.of(playerName), timeout, unit);
    }

    @Blocking
    @Override
    public @NotNull ItemStack awaitSkull(UUID playerUuid, long timeout, TimeUnit unit) {
        return this.awaitSkull(PlayerIdentification.of(playerUuid), timeout, unit);
    }

    @Blocking
    @Override
    public @NotNull SkullData awaitSkullData(UUID playerUuid, long timeout, TimeUnit unit) {
        return this.awaitSkullData(PlayerIdentification.of(playerUuid), timeout, unit);
    }

    @Blocking
    private ItemStack awaitSkull(PlayerIdentification identification, long timeout, TimeUnit unit) {
        return this.await(this.getSkullByIdentification(identification), timeout, unit, () -> this.creator.create(skullDataDefault.defaultSkullData()));
    }

    @Blocking
    private SkullData awaitSkullData(PlayerIdentification identification, long timeout, TimeUnit unit) {
        return this.await(this.getSkullDataByIdentification(identification), timeout, unit, skullDataDefault::defaultSkullData);
    }

    @Blocking
    private <T> T await(CompletableFuture<T> future, long timeout, TimeUnit unit, Supplier<T> defaultValue) {
        try {
            return future.get(timeout, unit);
        }
        catch (InterruptedException | ExecutionException | TimeoutException exception) {
            return defaultValue.get();
        }
    }

    @Override
    public void acceptSyncSkull(String playerName, Consumer<ItemStack> skullConsumer) {
        this.acceptSync(this.getSkull(playerName), skullConsumer);
    }

    @Override
    public void acceptAsyncSkull(String playerName, Consumer<ItemStack> skullConsumer) {
        this.acceptAsync(this.getSkull(playerName), skullConsumer);
    }

    @Override
    public void acceptSyncSkull(UUID playerUuid, Consumer<ItemStack> skullConsumer) {
        this.acceptSync(this.getSkull(playerUuid), skullConsumer);
    }

    @Override
    public void acceptAsyncSkull(UUID playerUuid, Consumer<ItemStack> skullConsumer) {
        this.acceptAsync(this.getSkull(playerUuid), skullConsumer);
    }

    @Override
    public void acceptSyncSkullData(String playerName, Consumer<SkullData> skullConsumer) {
        this.acceptSync(this.getSkullData(playerName), skullConsumer);

    }

    @Override
    public void acceptAsyncSkullData(String playerName, Consumer<SkullData> skullConsumer) {
        this.acceptAsync(this.getSkullData(playerName), skullConsumer);
    }

    @Override
    public void acceptSyncSkullData(UUID playerUuid, Consumer<SkullData> skullConsumer) {
        this.acceptSync(this.getSkullData(playerUuid), skullConsumer);
    }

    @Override
    public void acceptAsyncSkullData(UUID playerUuid, Consumer<SkullData> skullConsumer) {
        this.acceptAsync(this.getSkullData(playerUuid), skullConsumer);
    }

    @Override
    public void shutdown() {
        asyncExecutor.shutdown();
    }

    @Override
    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }

    @Override
    public SynchronizedExecutor getSyncExecutor() {
        return syncExecutor;
    }

    private <T> void acceptSync(CompletableFuture<T> future, Consumer<T> consumer) {
        this.acceptAsync(future, t -> this.syncExecutor.execute(() -> consumer.accept(t)));
    }

    private <T> void acceptAsync(CompletableFuture<T> future, Consumer<T> consumer) {
        future.whenComplete((t, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            consumer.accept(t);
        });
    }

    private CompletableFuture<ItemStack> getSkullByIdentification(PlayerIdentification identification) {
        return this.getSkullDataByIdentification(identification)
                .thenApplyAsync(creator::create, asyncExecutor);
    }

    private CompletableFuture<SkullData> getSkullDataByIdentification(PlayerIdentification identification) {
        CompletableFuture<SkullData> completableSkullData = new CompletableFuture<>();
        CompletableFuture<SkullData> completableSkullDataToCache = new CompletableFuture<>();
        CompletableFuture<SkullData> completableSkullDataToDataBase = new CompletableFuture<>();

        completableSkullDataToCache.thenAcceptAsync(skullData -> {
            identification.peek(name -> this.cachedSkullsByName.put(name, skullData), uuid -> this.cachedSkullsByUuid.put(uuid, skullData));
        }, asyncExecutor);

        completableSkullDataToDataBase.thenAcceptAsync(skullData -> {
            Instant expire = Instant.now().plus(this.dataBaseSaveExpire);

            this.database.saveSkullData(identification, skullData, expire);
        }, asyncExecutor);

        SkullData cachedSkull = identification.map(cachedSkullsByName::getIfPresent, cachedSkullsByUuid::getIfPresent);

        if (cachedSkull != null) {
            return CompletableFuture.completedFuture(cachedSkull);
        }

        // [Async] Get from Player
        playerExtractor.extractData(identification).whenComplete((optional, playerExtractError) -> {
            if (playerExtractError == null && optional.isPresent()) {
                completableSkullData.complete(optional.get());
                completableSkullDataToCache.complete(optional.get());
                completableSkullDataToDataBase.complete(optional.get());
                return;
            }

            // [Async] Get from Database
            database.extractData(identification).whenComplete((optionalData, databaseExtractError) -> {
                if (databaseExtractError == null && optionalData.isPresent()) {
                    completableSkullData.complete(optionalData.get());
                    completableSkullDataToCache.complete(optionalData.get());
                    return;
                }

                // [Async] Get from API
                apiExtractor.extractData(identification).whenComplete((optionalApi, apiExtractError) -> {
                    if (apiExtractError == null && optionalApi.isPresent()) {
                        completableSkullData.complete(optionalApi.get());
                        completableSkullDataToCache.complete(optionalApi.get());
                        completableSkullDataToDataBase.complete(optionalApi.get());
                        return;
                    }


                    // Get default skull data
                    completableSkullData.complete(skullDataDefault.defaultSkullData());
                });
            });
        });

        return completableSkullData;
    }

}
