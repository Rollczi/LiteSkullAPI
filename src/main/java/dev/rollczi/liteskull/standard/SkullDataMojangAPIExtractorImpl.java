/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.rollczi.liteskull.api.PlayerIdentification;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class SkullDataMojangAPIExtractorImpl implements SkullDataAPIExtractor {

    private static final String MOJANG_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String MOJANG_SIGNATURE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    private static final Gson GSON = new Gson();

    private final int limitMojang;
    private final Cache<UUID, Boolean> lastRequests;

    private Executor executor;

    public SkullDataMojangAPIExtractorImpl(int threadPool, int limitMojang, Duration expireRequests) {
        this.limitMojang = limitMojang;
        this.lastRequests = CacheBuilder.newBuilder()
                .expireAfterWrite(expireRequests.get(ChronoUnit.SECONDS), TimeUnit.SECONDS)
                .build();
        this.executor = Executors.newFixedThreadPool(threadPool);
    }

    @Override
    public CompletableFuture<Optional<SkullData>> extractData(PlayerIdentification identification) {
        if (lastRequests.size() >= limitMojang) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                lastRequests.put(UUID.randomUUID(), true);

                Optional<String> optionalUuid = this.extractUuid(identification);

                if (!optionalUuid.isPresent()) {
                    return Optional.empty();
                }

                String uuid = optionalUuid.get();
                String signature = readURLContent(String.format(MOJANG_SIGNATURE_URL, uuid));

                if (signature.isEmpty()) {
                    return Optional.empty();
                }

                JsonObject jsonObject = GSON.fromJson(signature, JsonObject.class);

                String valueCoded = jsonObject.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
                String decoded = new String(Base64.getDecoder().decode(valueCoded));

                jsonObject = GSON.fromJson(decoded, JsonObject.class);

                String skinURL = jsonObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
                byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();

                String value = new String(Base64.getEncoder().encode(skinByte));

                return Optional.of(new SkullData(SkullUtils.DEFAULT_SIGNATURE, value));
            } catch (Exception ignore) {}

            return Optional.empty();
        }, executor);
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
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

    private Optional<String> extractUuid(PlayerIdentification identification) {
        return identification.map(name -> {
            String result = readURLContent(String.format(MOJANG_URL, name));

            if (result.isEmpty()) {
                return Optional.empty();
            }

            JsonObject jsonObject = GSON.fromJson(result, JsonObject.class);
            String replace = jsonObject.get("id").toString().replace("\"", "");

            return Optional.of(replace);
        }, uuid1 -> Optional.of(uuid1.toString()));
    }
    
}
