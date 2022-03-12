/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.rollczi.liteskull.api.SkullData;
import dev.rollczi.liteskull.api.extractor.SkullDataAPIExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SkullDataMojangAPIExtractorImpl implements SkullDataAPIExtractor {

    private final int limitMojang;
    private final Cache<UUID, String> lastRequests;
    public SkullDataMojangAPIExtractorImpl(int limitMojang, Duration expireRequests) {
        this.limitMojang = limitMojang;
        this.lastRequests = CacheBuilder.newBuilder()
                .expireAfterWrite(expireRequests.get(ChronoUnit.SECONDS), TimeUnit.SECONDS)
                .build();

    }

    @Override
    public CompletableFuture<Optional<SkullData>> extractData(String player) {
        if (lastRequests.size() >= limitMojang) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                lastRequests.put(UUID.randomUUID(), player);
                String result = readURLContent("https://api.mojang.com/users/profiles/minecraft/" + player);

                if (result.isEmpty()) {
                    return Optional.empty();
                }

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(result, JsonObject.class);
                String uid = jsonObject.get("id").toString().replace("\"", "");
                String signature = readURLContent("https://sessionserver.mojang.com/session/minecraft/profile/" + uid);

                if (signature.isEmpty()) {
                    return Optional.empty();
                }

                jsonObject = gson.fromJson(signature, JsonObject.class);

                String valueCoded = jsonObject.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
                String decoded = new String(Base64.getDecoder().decode(valueCoded));

                jsonObject = gson.fromJson(decoded, JsonObject.class);

                String skinURL = jsonObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
                byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();

                String value = new String(Base64.getEncoder().encode(skinByte));

                return Optional.of(new SkullData(SkullUtils.DEFAULT_SIGNATURE, value));
            } catch (Exception ignore) {}

            return Optional.empty();
        });
    }

    private String readURLContent(String urlStr) {
        StringBuilder builder = new StringBuilder();

        try {
            URL url = new URL(urlStr);

            try (InputStream streamUrl = url.openStream();
                 InputStreamReader reader = new InputStreamReader(streamUrl, StandardCharsets.UTF_8);
                 BufferedReader in = new BufferedReader(reader)) {

                while (true) {
                    String line = in.readLine();

                    if (line == null) {
                        break;
                    }

                    builder.append(line);
                }
            }
        } catch (IOException ignored) {}

        return builder.toString();
    }
    
}
