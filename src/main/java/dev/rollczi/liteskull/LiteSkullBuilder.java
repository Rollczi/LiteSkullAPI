/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull;

import dev.rollczi.liteskull.api.SkullAPI;
import dev.rollczi.liteskull.api.SkullCreator;
import dev.rollczi.liteskull.api.SynchronizedExecutor;
import dev.rollczi.liteskull.api.extractor.SkullDataDefault;
import dev.rollczi.liteskull.api.extractor.SkullDataAPIExtractor;
import dev.rollczi.liteskull.api.extractor.SkullDataPlayerExtractor;
import dev.rollczi.liteskull.api.SkullData;
import dev.rollczi.liteskull.api.extractor.SkullDatabase;
import dev.rollczi.liteskull.standard.BukkitSynchronizedSchedulerImpl;
import dev.rollczi.liteskull.standard.SkullCacheDatabase;
import dev.rollczi.liteskull.standard.SkullCreatorImpl;
import dev.rollczi.liteskull.standard.SkullDataMojangAPIExtractorImpl;
import dev.rollczi.liteskull.standard.SkullDataDefaultImpl;
import dev.rollczi.liteskull.standard.SkullDataPlayerExtractorImpl;
import dev.rollczi.liteskull.standard.SynchronizedSchedulerImpl;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.concurrent.Executors;

public class LiteSkullBuilder {

    private Duration cacheExpireAfterWrite = Duration.ofMinutes(15L);
    private Duration cacheExpireAfterAccess = Duration.ZERO;
    private Duration dataBaseSaveExpire = Duration.ofMinutes(60L);

    private SkullDataPlayerExtractor playerExtractor = new SkullDataPlayerExtractorImpl();
    private SkullDatabase database = new SkullCacheDatabase();
    private SkullDataAPIExtractor apiExtractor = new SkullDataMojangAPIExtractorImpl(300, Duration.ofMinutes(10));
    private SkullDataDefault skullDataDefault = new SkullDataDefaultImpl();
    private SkullCreator creator = new SkullCreatorImpl();
    private SynchronizedExecutor syncExecutor = new SynchronizedSchedulerImpl();
    private int threadPool = 20;

    public LiteSkullBuilder cacheExpireAfterWrite(Duration duration) {
        this.cacheExpireAfterWrite = duration;
        return this;
    }

    public LiteSkullBuilder cacheExpireAfterAccess(Duration duration) {
        this.cacheExpireAfterAccess = duration;
        return this;
    }

    public LiteSkullBuilder dataBaseSaveExpire(Duration duration) {
        this.dataBaseSaveExpire = duration;
        return this;
    }

    public LiteSkullBuilder playerExtractor(SkullDataPlayerExtractor playerExtractor) {
        this.playerExtractor = playerExtractor;
        return this;
    }

    public LiteSkullBuilder database(SkullDatabase database) {
        this.database = database;
        return this;
    }

    public LiteSkullBuilder apiExtractor(SkullDataAPIExtractor apiExtractor) {
        this.apiExtractor = apiExtractor;
        return this;
    }

    public LiteSkullBuilder defaultSkull(SkullDataDefault skullDataDefault) {
        this.skullDataDefault = skullDataDefault;
        return this;
    }

    public LiteSkullBuilder defaultSkull(SkullData skullData) {
        this.skullDataDefault = () -> skullData;
        return this;
    }

    public LiteSkullBuilder creator(SkullCreator creator) {
        this.creator = creator;
        return this;
    }

    public LiteSkullBuilder scheduler(SynchronizedExecutor syncExecutor) {
        this.syncExecutor = syncExecutor;
        return this;
    }

    public LiteSkullBuilder threadPool(int threadPool) {
        this.threadPool = threadPool;
        return this;
    }

    public LiteSkullBuilder bukkitScheduler(Plugin plugin) {
        this.syncExecutor = new BukkitSynchronizedSchedulerImpl(plugin);
        return this;
    }


    public SkullAPI build() {
        return new LiteSkullAPI(
                playerExtractor,
                database,
                dataBaseSaveExpire,
                apiExtractor,
                skullDataDefault,
                creator,
                cacheExpireAfterWrite,
                cacheExpireAfterAccess,
                syncExecutor,
                Executors.newFixedThreadPool(threadPool)
        );
    }

    public static LiteSkullBuilder builder() {
        return new LiteSkullBuilder();
    }

}
