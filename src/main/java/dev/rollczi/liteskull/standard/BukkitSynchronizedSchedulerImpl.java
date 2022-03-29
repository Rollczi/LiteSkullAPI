/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import dev.rollczi.liteskull.api.SynchronizedExecutor;
import org.bukkit.plugin.Plugin;

class BukkitSynchronizedSchedulerImpl implements SynchronizedExecutor {

    private final Plugin plugin;

    public BukkitSynchronizedSchedulerImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Runnable command) {
        this.plugin.getServer().getScheduler().runTask(plugin, command);
    }

}
