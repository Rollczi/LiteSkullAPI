/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi;

import org.bukkit.inventory.ItemStack;

public interface SkullCreator {

    ItemStack create(SkullData data);

}
