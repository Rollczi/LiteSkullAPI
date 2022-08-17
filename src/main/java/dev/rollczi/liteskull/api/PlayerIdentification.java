/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.api;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlayerIdentification {

    @Nullable
    private final String name;

    @Nullable
    private final UUID uuid;

    private PlayerIdentification(@Nullable String name, @Nullable UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(this.name);
    }

    public Optional<UUID> getUuid() {
        return Optional.ofNullable(this.uuid);
    }

    public <T> T map(Function<String, T> nameMapper, Function<UUID, T> uuidMapper) {
        if (this.name != null) {
            return nameMapper.apply(this.name);
        }

        if (this.uuid != null) {
            return uuidMapper.apply(this.uuid);
        }

        throw new IllegalStateException();
    }

    public void peek(Consumer<String> nameConsumer, Consumer<UUID> uuidConsumer) {
        if (this.name != null) {
            nameConsumer.accept(this.name);
            return;
        }

        if (this.uuid != null) {
            uuidConsumer.accept(this.uuid);
            return;
        }

        throw new IllegalStateException();
    }

    public static PlayerIdentification of(String name) {
        return new PlayerIdentification(name, null);
    }

    public static PlayerIdentification of(UUID uuid) {
        return new PlayerIdentification(null, uuid);
    }

}
