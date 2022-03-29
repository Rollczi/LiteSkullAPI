/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.rollczi.liteskull.api.SkullData;
import dev.rollczi.liteskull.api.extractor.SkullDataPlayerExtractor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

class SkullDataPlayerExtractorImpl implements SkullDataPlayerExtractor {

    @Override
    public CompletableFuture<Optional<SkullData>> extractData(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<? extends Player> optional = Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getName().equalsIgnoreCase(playerName))
                    .findAny();

            if (!optional.isPresent()) {
                return Optional.empty();
            }

            Player player = optional.get();

            try {

                Method getProfileMethod = player.getClass().getMethod("getProfile");
                GameProfile gameProfile = (GameProfile) getProfileMethod.invoke(player);
                Property property = gameProfile.getProperties().get("textures").iterator().next();

                return Optional.of(new SkullData(property.getSignature(), property.getValue()));
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                exception.printStackTrace();
            }
            catch (NoSuchElementException ignored) {
                return Optional.empty();
            }

            return Optional.empty();
        });
    }

}
