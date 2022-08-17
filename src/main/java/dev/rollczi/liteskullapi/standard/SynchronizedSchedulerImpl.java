/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.standard;

import dev.rollczi.liteskullapi.SynchronizedExecutor;
import org.jetbrains.annotations.NotNull;

class SynchronizedSchedulerImpl implements SynchronizedExecutor {

    @Override
    public void execute(@NotNull Runnable runnable) {
        throw new UnsupportedOperationException("SyncExecutor is not implement! User LiteSkullBuilder#scheduler() or LiteSkullBuilder#bukkitScheduler()");
    }

}
