/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.standard;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.rollczi.liteskullapi.PlayerIdentification;
import dev.rollczi.liteskullapi.SkullData;
import dev.rollczi.liteskullapi.extractor.SkullDataAPIExtractor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class SkullDataMojangAPIExtractorImpl implements SkullDataAPIExtractor {

    private static final String GET_PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String GET_FULL_PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

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

        lastRequests.put(UUID.randomUUID(), true);
        return CompletableFuture.supplyAsync(() -> this.fetchUuid(identification)
            .flatMap(uuid -> fetchTexture(uuid)), executor);
    }

    private Optional<SkullData> fetchTexture(String uuid) {
        Optional<MojangFullProfile> fullProfile = readUrlContent(String.format(GET_FULL_PROFILE_URL, uuid), MojangFullProfile.class);
        Optional<String> textureBase64 = fullProfile.flatMap(profile -> profile.properties.stream()
            .filter(property -> property.name.equals("textures"))
            .map(property -> property.value)
            .findFirst()
        );

        if (!textureBase64.isPresent()) {
            return Optional.empty();
        }

        MojangFullProfile profile = fullProfile.get();
        String skinTextureBase64 = extractSkinTexture(textureBase64.get());
        return Optional.of(new SkullData(profile.name, SkullUtils.DEFAULT_SIGNATURE, skinTextureBase64));
    }

    private static String extractSkinTexture(String textureBase64) {
        String textureJson = new String(Base64.getDecoder().decode(textureBase64));
        JsonObject texture = GSON.fromJson(textureJson, JsonObject.class);

        String skinUrl = texture.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
        String skullJson = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinUrl + "\"}}}");
        byte[] skullBase64 = Base64.getEncoder().encode(skullJson.getBytes());
        return new String(skullBase64);
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    private Optional<String> fetchUuid(PlayerIdentification identification) {
        return identification.map(name -> {
            return readUrlContent(String.format(GET_PROFILE_URL, name), MojangProfile.class)
                .map(profile -> profile.id);
        }, uuid -> Optional.of(uuid.toString()));
    }

    private <T> Optional<T> readUrlContent(String urlStr, Class<T> type) {
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
        } catch (IOException ignored) {
        }

        if (builder.length() == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(GSON.fromJson(builder.toString(), type));
    }

    public static class MojangProfile {
        private final String id;

        public MojangProfile(String id) {
            this.id = id;
        }
    }

    private static class MojangFullProfile {
        private final String name;
        private final List<Property> properties;

        public MojangFullProfile(String name, List<Property> properties) {
            this.name = name;
            this.properties = properties;
        }

        public static class Property {
            private final String name;
            private final String value;

            public Property(String name, String value) {
                this.name = name;
                this.value = value;
            }
        }
    }

}
