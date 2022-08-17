/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import dev.rollczi.liteskull.api.SkullCreator;
import dev.rollczi.liteskull.api.SkullData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

class SkullCreatorImpl implements SkullCreator {

    @Override
    public ItemStack create(SkullData data) {
        try {
            return applyOnItem(new ItemStack(Material.valueOf("PLAYER_HEAD")), data);
        } catch (IllegalArgumentException ignored) {}

        return applyOnItem(new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3), data);
    }

    @Override
    public ItemStack applyOnItem(ItemStack item, SkullData data) {
        try {
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

            if (skullMeta == null) {
                return item;
            }

            UUID uuid = UUID.nameUUIDFromBytes((data.getValue() + data.getSignature()).getBytes());
            GameProfile gameProfile = new GameProfile(uuid, null);
            PropertyMap properties = gameProfile.getProperties();
            Property property = new Property("textures", data.getValue(), data.getSignature());

            properties.put("textures", property);

            try {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");

                profileField.setAccessible(true);
                profileField.set(skullMeta, gameProfile);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
                exception.printStackTrace();
            }

            item.setItemMeta(skullMeta);
            return item;
        }
        catch (NullPointerException exception) {
            return item;
        }
    }

}
