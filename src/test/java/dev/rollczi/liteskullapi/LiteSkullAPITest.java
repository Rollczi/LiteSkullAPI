/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi;

import dev.rollczi.liteskullapi.test.TestSkullDataAPIExtractor;
import java.util.concurrent.ExecutorService;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.*;
import static org.awaitility.Awaitility.*;

class LiteSkullAPITest {

    private final SkullAPI api = LiteSkullFactory.builder()
        .scheduler(command -> {
            Thread thread = new Thread(command);

            thread.setName("sync");
            thread.start();
        })
        .creator(data -> new ItemStack(Material.PLAYER_HEAD))
        .apiExtractor(new TestSkullDataAPIExtractor())
        .build();

    @Test
    void getSkull() {
        CompletableFuture<ItemStack> skull = api.getSkull("");

        await().atMost(1, SECONDS).until(() -> this.isOk(skull));
    }

    @Test
    void getSkullByUuid() {
        CompletableFuture<ItemStack> skull = api.getSkull(UUID.randomUUID());

        await().atMost(1, SECONDS).until(() -> this.isOk(skull));
    }

    @Test
    void getSkullData() {
        CompletableFuture<SkullData> skullData = api.getSkullData("");

        await().atMost(1, SECONDS).until(() -> this.isOk(skullData));
    }

    @Test
    void getSkullDataByUuid() {
        CompletableFuture<SkullData> skullData = api.getSkullData(UUID.randomUUID());

        await().atMost(1, SECONDS).until(() -> this.isOk(skullData));
    }

    private boolean isOk(CompletableFuture<?> future) {
        return future.isDone() && !future.isCompletedExceptionally() && !future.isCancelled();
    }

    @Test
    void acceptSkull() {
        Result result = new Result();

        api.acceptSkull("test", item -> this.completeResult(result));
        result.assertSynchronously();
    }

    @Test
    void acceptSkullByUuid() {
        Result result = new Result();

        api.acceptSkull(UUID.randomUUID(), item -> this.completeResult(result));
        result.assertSynchronously();
    }

    @Test
    void acceptSkullData() {
        Result result = new Result();

        api.acceptSkullData("test", skullData -> this.completeResult(result));
        result.assertSynchronously();
    }

    @Test
    void acceptSkullDataByUuid() {
        Result result = new Result();

        api.acceptSkullData(UUID.randomUUID(), skullData -> this.completeResult(result));
        result.assertSynchronously();
    }

    @Test
    void testShutdown() {
        SkullAPI build = LiteSkullFactory.builder()
            .build();

        build.shutdown();

        assertThat(build).isInstanceOf(LiteSkullAPI.class)
            .isNotNull()
            .isInstanceOf(LiteSkullAPI.class)
            .extracting("asyncExecutor")
            .asInstanceOf(type(ExecutorService.class))
            .returns(true, executorService -> executorService.isShutdown());
    }


    private void completeResult(Result result) {
        if (Thread.currentThread().getName().equals("sync")) {
            result.markSynchronously();
        } else {
            result.markAsynchronously();
        }
    }

    static class Result {
        private boolean completed = false;
        private boolean synchronously = false;

        void markSynchronously() {
            this.synchronously = true;
            this.completed = true;
        }

        void markAsynchronously() {
            this.synchronously = false;
            this.completed = true;
        }

        void assertSynchronously() {
            await().atMost(3, SECONDS).until(() -> this.completed && this.synchronously);
        }

    }

}
