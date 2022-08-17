/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.skullplugin;

import dev.rollczi.liteskullapi.LiteSkullFactory;
import dev.rollczi.liteskullapi.SkullAPI;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class SkullPlugin extends JavaPlugin {

    private SkullAPI skullAPI;

    @Override
    public void onEnable() {
        skullAPI = LiteSkullFactory.builder()
                .bukkitScheduler(this)
                .build();

        PluginCommand command = this.getCommand("give-skull");

        if (command == null) {
            throw new IllegalStateException();
        }

        command.setExecutor(new SkullCommand(skullAPI));
    }

    @Override
    public void onDisable() {
        skullAPI.shutdown();
    }
}
