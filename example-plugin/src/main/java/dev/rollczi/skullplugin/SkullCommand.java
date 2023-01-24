/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.skullplugin;

import dev.rollczi.liteskullapi.SkullAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkullCommand implements CommandExecutor {

    private final SkullAPI skullAPI;

    public SkullCommand(SkullAPI skullAPI) {
        this.skullAPI = skullAPI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        this.skullAPI.acceptSyncSkull(player.getName(), itemStack -> player.getInventory().addItem(itemStack));

        return true;
    }

}