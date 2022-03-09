/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import dev.rollczi.liteskull.api.SynchronizedExecutor;

public class SynchronizedSchedulerImpl implements SynchronizedExecutor {

    @Override
    public void execute(Runnable runnable) {
        throw new UnsupportedOperationException("SyncExecutor is not implement! User LiteSkullBuilder#scheduler() or LiteSkullBuilder#bukkitScheduler()");
    }

}
