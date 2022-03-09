/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.api;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface SkullAPI {

    @NotNull
    CompletableFuture<ItemStack> getSkull(String playerName);

    @NotNull
    CompletableFuture<SkullData> getSkullData(String playerName);

    @NotNull
    @Blocking
    ItemStack awaitForSkull(String playerName, long timeout, TimeUnit unit);

    @NotNull
    @Blocking
    SkullData awaitForSkullData(String playerName, long timeout, TimeUnit unit);

    void acceptSyncSkull(String playerName, Consumer<ItemStack> skullConsumer);

    void acceptAsyncSkull(String playerName, Consumer<ItemStack> skullConsumer);

    void acceptSyncSkullData(String playerName, Consumer<SkullData> skullConsumer);

    void acceptAsyncSkullData(String playerName, Consumer<SkullData> skullConsumer);

}
