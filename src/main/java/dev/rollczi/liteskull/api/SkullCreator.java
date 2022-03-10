/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.api;

import org.bukkit.inventory.ItemStack;

public interface SkullCreator {

    ItemStack create(SkullData data);

    ItemStack applyOnItem(ItemStack item, SkullData data);

}
