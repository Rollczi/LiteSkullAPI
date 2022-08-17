/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.rollczi.liteskull.LiteSkullAPI;
import dev.rollczi.liteskull.api.SkullAPI;
import dev.rollczi.liteskull.api.SkullCreator;
import dev.rollczi.liteskull.api.SynchronizedExecutor;
import dev.rollczi.liteskull.api.extractor.SkullDataDefault;
import dev.rollczi.liteskull.api.extractor.SkullDataAPIExtractor;
import dev.rollczi.liteskull.api.extractor.SkullDataPlayerExtractor;
import dev.rollczi.liteskull.api.SkullData;
import dev.rollczi.liteskull.api.extractor.SkullDatabase;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LiteSkullBuilder {

    private Logger logger = Logger.getLogger("LiteSkullAPI");

    private final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setUncaughtExceptionHandler((thread, throwable) -> logger.log(Level.SEVERE, "Uncaught exception during task execute", throwable))
            .setNameFormat("LiteSkullAPI %d")
            .build();

    private Duration cacheExpireAfterWrite = Duration.ofMinutes(15L);
    private Duration cacheExpireAfterAccess = Duration.ZERO;
    private Duration dataBaseSaveExpire = Duration.ofMinutes(60L);

    private SkullDataPlayerExtractor playerExtractor = new SkullDataPlayerExtractorImpl(8);
    private SkullDatabase database = new SkullCacheDatabase();
    private SkullDataAPIExtractor apiExtractor = new SkullDataMojangAPIExtractorImpl(8, 300, Duration.ofMinutes(10));
    private SkullDataDefault skullDataDefault = new SkullDataDefaultImpl();
    private SkullCreator creator = new SkullCreatorImpl();
    private SynchronizedExecutor syncExecutor = new SynchronizedSchedulerImpl();
    private int threadPool = 8;

    public LiteSkullBuilder logger(Logger logger) {
        this.logger = logger;
        return this;
    }

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
        ExecutorService executorService = Executors.newFixedThreadPool(threadPool, threadFactory);

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
                executorService
        );
    }

    public static LiteSkullBuilder builder() {
        return new LiteSkullBuilder();
    }

}
