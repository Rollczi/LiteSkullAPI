/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface SkullAPI {

    @NotNull
    CompletableFuture<ItemStack> getSkull(String playerName);

    @NotNull
    CompletableFuture<ItemStack> getSkull(UUID playerUuid);

    @NotNull
    CompletableFuture<SkullData> getSkullData(String playerName);

    @NotNull
    CompletableFuture<SkullData> getSkullData(UUID playerUuid);

    void acceptSkull(String playerName, Consumer<ItemStack> skullConsumer);

    void acceptSkull(UUID playerUuid, Consumer<ItemStack> skullConsumer);

    void acceptSkullData(String playerName, Consumer<SkullData> skullConsumer);

    void acceptSkullData(UUID playerUuid, Consumer<SkullData> skullConsumer);

    void shutdown();

}
