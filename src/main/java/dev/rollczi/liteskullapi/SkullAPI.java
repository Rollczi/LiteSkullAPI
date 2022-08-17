/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface SkullAPI {

    @NotNull
    CompletableFuture<ItemStack> getSkull(String playerName);

    @NotNull
    CompletableFuture<ItemStack> getSkull(UUID playerUuid);

    @NotNull
    CompletableFuture<SkullData> getSkullData(String playerName);

    @NotNull
    CompletableFuture<SkullData> getSkullData(UUID playerUuid);

    @NotNull
    @Blocking
    ItemStack awaitSkull(String playerName, long timeout, TimeUnit unit);

    @NotNull
    @Blocking
    SkullData awaitSkullData(String playerName, long timeout, TimeUnit unit);

    @NotNull
    @Blocking
    ItemStack awaitSkull(UUID playerUuid, long timeout, TimeUnit unit);

    @NotNull
    @Blocking
    SkullData awaitSkullData(UUID playerUuid, long timeout, TimeUnit unit);

    void acceptSyncSkull(String playerName, Consumer<ItemStack> skullConsumer);

    void acceptAsyncSkull(String playerName, Consumer<ItemStack> skullConsumer);

    void acceptSyncSkull(UUID playerUuid, Consumer<ItemStack> skullConsumer);

    void acceptAsyncSkull(UUID playerUuid, Consumer<ItemStack> skullConsumer);

    void acceptSyncSkullData(String playerName, Consumer<SkullData> skullConsumer);

    void acceptAsyncSkullData(String playerName, Consumer<SkullData> skullConsumer);

    void acceptSyncSkullData(UUID playerUuid, Consumer<SkullData> skullConsumer);

    void acceptAsyncSkullData(UUID playerUuid, Consumer<SkullData> skullConsumer);

    void shutdown();

    ExecutorService getAsyncExecutor();

    SynchronizedExecutor getSyncExecutor();

}
