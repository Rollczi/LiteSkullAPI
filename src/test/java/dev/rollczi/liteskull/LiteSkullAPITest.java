/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull;

import dev.rollczi.liteskull.api.SkullAPI;
import dev.rollczi.liteskull.api.SkullData;
import dev.rollczi.liteskull.test.TestSkullDataAPIExtractor;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.*;
import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LiteSkullAPITest {

    private final SkullAPI api = LiteSkullFactory.builder()
            .scheduler(command -> {
                Thread thread = new Thread(command);

                thread.setName("sync");
                thread.start();
            })
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
    void awaitSkull() {
        assertNotNull(api.awaitSkull("", 1, SECONDS));
    }

    @Test
    void awaitSkullData() {
        assertNotNull(api.awaitSkullData("", 1, SECONDS));
    }

    @Test
    void awaitSkullByUuid() {
        assertNotNull(api.awaitSkull(UUID.randomUUID(), 1, SECONDS));
    }

    @Test
    void awaitSkullDataByUuid() {
        assertNotNull(api.awaitSkullData(UUID.randomUUID(), 1, SECONDS));
    }

    @Test
    void acceptSyncSkull() {
        Result result = new Result();

        api.acceptSyncSkull("test", item -> this.completeResult(result));
        result.assertSynchronously();
    }

    @Test
    void acceptAsyncSkull() {
        Result result = new Result();

        api.acceptAsyncSkull("test", item -> this.completeResult(result));
        result.assertAsynchronously();
    }

    @Test
    void acceptSyncSkullByUuid() {
        Result result = new Result();

        api.acceptSyncSkull(UUID.randomUUID(), item -> this.completeResult(result));
        result.assertSynchronously();
    }

    @Test
    void acceptAsyncSkullByUuid() {
        Result result = new Result();

        api.acceptAsyncSkull(UUID.randomUUID(), item -> this.completeResult(result));
        result.assertAsynchronously();
    }

    @Test
    void acceptSyncSkullData() {
        Result result = new Result();

        api.acceptSyncSkullData("test", skullData -> this.completeResult(result));
        result.assertSynchronously();
    }

    @Test
    void acceptAsyncSkullData() {
        Result result = new Result();

        api.acceptAsyncSkullData("test", skullData -> this.completeResult(result));
        result.assertAsynchronously();
    }

    @Test
    void acceptSyncSkullDataByUuid() {
        Result result = new Result();

        api.acceptSyncSkullData(UUID.randomUUID(), skullData -> this.completeResult(result));
        result.assertSynchronously();
    }

    @Test
    void acceptAsyncSkullDataByUuid() {
        Result result = new Result();

        api.acceptAsyncSkullData(UUID.randomUUID(), skullData -> this.completeResult(result));
        result.assertAsynchronously();
    }

    @Test
    void testShutdown() {
        SkullAPI build = LiteSkullFactory.builder()
                .build();

        build.shutdown();

        assertTrue(build.getAsyncExecutor().isShutdown());
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
            await().atMost(1, SECONDS).until(() -> this.completed && this.synchronously);
        }

        void assertAsynchronously() {
            await().atMost(1, SECONDS).until(() -> this.completed && !this.synchronously);
        }

    }

}
