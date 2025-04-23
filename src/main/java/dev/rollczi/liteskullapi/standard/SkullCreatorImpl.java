/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.standard;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import dev.rollczi.liteskullapi.SkullCreator;
import dev.rollczi.liteskullapi.SkullData;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.Nullable;

class SkullCreatorImpl implements SkullCreator {

    private static final Gson GSON = new Gson();

    @Override
    public ItemStack create(SkullData data) {
        ItemStack skullItem = createSkullItem();
        return applyTexture(skullItem, data);
    }

    private ItemStack createSkullItem() {
        try {
            return new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } catch (IllegalArgumentException ignored) {}

        return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
    }

    public ItemStack applyTexture(ItemStack item, SkullData data) {
        @Nullable ItemMeta itemMeta = item.getItemMeta();
        if (!(itemMeta instanceof SkullMeta)) {
            throw new NullPointerException("Cannot get SkullMeta from item: " + item);
        }

        UUID profileId = UUID.nameUUIDFromBytes((data.getTexture() + data.getSignature()).getBytes());
        item.setItemMeta(editMeta((SkullMeta) itemMeta, profileId, data));
        return item;
    }

    private SkullMeta editMeta(SkullMeta skullMeta, UUID profileId, SkullData data) {
        if (Versions.isSupported(Versions.V1_20_1)) {
            return editMeta1_20_1(skullMeta, profileId, data);
        }

        GameProfile gameProfile = new GameProfile(profileId, data.getName());
        PropertyMap properties = gameProfile.getProperties();
        Property property = new Property("textures", data.getTexture(), data.getSignature());

        properties.put("textures", property);

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");

            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return skullMeta;
    }

    private SkullMeta editMeta1_20_1(SkullMeta skullMeta, UUID profileId, SkullData data) {
        String textureUrl = getSkinUrl(data.getTexture());
        if (textureUrl == null) {
            return skullMeta;
        }

        PlayerProfile profile = Bukkit.createPlayerProfile(profileId, data.getName());
        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(new URL(textureUrl));
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
            return skullMeta;
        }

        profile.setTextures(textures);
        skullMeta.setOwnerProfile(profile);
        return skullMeta;
    }

    public static String getSkinUrl(String base64Texture) {
        String decoded = new String(Base64.getDecoder().decode(base64Texture));
        JsonObject object = GSON.fromJson(decoded, JsonObject.class);
        JsonElement textures = object.get("textures");
        if (!(textures instanceof JsonObject)) {
            return null;
        }

        JsonElement skin = textures.getAsJsonObject().get("SKIN");
        if (skin == null) {
            return null;
        }

        JsonElement url = skin.getAsJsonObject().get("url");
        return url == null ? null : url.getAsString();
    }

}
