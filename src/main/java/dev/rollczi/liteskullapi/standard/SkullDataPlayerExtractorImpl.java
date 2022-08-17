/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.standard;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.rollczi.liteskullapi.PlayerIdentification;
import dev.rollczi.liteskullapi.SkullAPIException;
import dev.rollczi.liteskullapi.SkullData;
import dev.rollczi.liteskullapi.extractor.SkullDataPlayerExtractor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class SkullDataPlayerExtractorImpl implements SkullDataPlayerExtractor {

    private Executor executor;

    SkullDataPlayerExtractorImpl(int pool) {
        this.executor = Executors.newFixedThreadPool(pool);
    }

    @Override
    public CompletableFuture<Optional<SkullData>> extractData(PlayerIdentification identification) {
        return CompletableFuture.supplyAsync(() -> {
            Player player = identification.map(Bukkit::getPlayer, Bukkit::getPlayer);

            if (player == null) {
                return Optional.empty();
            }

            try {

                Method getProfileMethod = player.getClass().getMethod("getProfile");
                GameProfile gameProfile = (GameProfile) getProfileMethod.invoke(player);
                Property property = gameProfile.getProperties().get("textures").iterator().next();

                return Optional.of(new SkullData(property.getSignature(), property.getValue()));
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                throw new SkullAPIException("Can't extract texture from player", exception);
            }
            catch (NoSuchElementException ignored) {
                return Optional.empty();
            }
        }, executor);
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

}
