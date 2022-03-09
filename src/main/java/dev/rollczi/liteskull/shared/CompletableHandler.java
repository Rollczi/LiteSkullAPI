/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.shared;

import dev.rollczi.liteskull.exception.SkullAPIException;
import dev.rollczi.liteskull.exception.SkullExceptionallyHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

public class CompletableHandler {

    private final Executor executor;

    public CompletableHandler(Executor executor) {
        this.executor = executor;
    }

    public <T> Handler<T> of(CompletableFuture<T> completable) {
        return new Handler<>(completable, executor);
    }

    public static class Handler<T> {
        private final CompletableFuture<T> completable;
        private final Executor executor;

        public Handler(CompletableFuture<T> completable, Executor executor) {
            this.completable = completable;
            this.executor = executor;
        }

        public <U> Handler<U> applyAsync(Function<T, U> function) {
            CompletableFuture<U> future = this.completable.handleAsync((t, throwable) -> {
                if (throwable != null) {
                    throw new SkullAPIException(throwable);
                }

                return function.apply(t);
            }, executor).exceptionally(new SkullExceptionallyHandler<>());

            return new Handler<>(future, executor);
        }

        public Handler<T> acceptAsync(Consumer<T> consumer) {
            return this.applyAsync(t -> {
                consumer.accept(t);
                return t;
            });
        }

    }

}
