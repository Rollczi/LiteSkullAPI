/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.standard;

import dev.rollczi.liteskullapi.SynchronizedExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

class BukkitSynchronizedSchedulerImpl implements SynchronizedExecutor {

    private final Plugin plugin;

    public BukkitSynchronizedSchedulerImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Runnable command) {
        this.plugin.getServer().getScheduler().runTask(plugin, command);
    }

}
