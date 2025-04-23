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
        if (!(sender instanceof Player player)) {
            return true;
        }

        String name = args.length > 0 ? args[0] : player.getName();
        this.skullAPI.acceptSkull(name, itemStack -> player.getInventory().addItem(itemStack));
        return true;
    }

}