package com.inappstory.sdk.lrudiskcache;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_1;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_200;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_50;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_500;

import android.content.Context;
import android.os.Handler;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.cache.vod.EmptyVODCacheJournal;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournal;
import com.inappstory.sdk.utils.ScheduledTPEManager;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class LruCachesHolder implements ILruCachesHolder {
    private final LruDiskCache dummyCache = new DummyLruDiskCache();
    private volatile LruDiskCache fastCache = dummyCache;
    private volatile LruDiskCache commonCache = dummyCache;
    private volatile LruDiskCache infiniteCache = dummyCache;
    private volatile LruDiskCache bundleCache = dummyCache;
    private volatile LruDiskCache vodCache = dummyCache;
    private final Context context;
    private final int cacheSize;
    private long commonCacheSize = MB_100;
    private long fastCacheSize = MB_10;
    private final AtomicBoolean isReinitializing = new AtomicBoolean(false);
    private final ScheduledTPEManager checkSpaceThread = new ScheduledTPEManager();

    private final ExecutorService initExecutor = Executors.newFixedThreadPool(1);

    private VODCacheJournal vodCacheJournal = new EmptyVODCacheJournal();

    public LruCachesHolder(
            final IASCore core,
            final int cacheSize
    ) {
        this.context = core.appContext().getApplicationContext();
        this.cacheSize = cacheSize;
        initExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    initializeCaches();
                } catch (Exception ignored) {

                }
                try {
                    initializeVODJournal();
                } catch (Exception ignored) {

                }
                runFreeSpaceCheck();
            }
        });

    }

    void initializeVODJournal() {
        File file = new File(
                context.getFilesDir() +
                        File.separator +
                        "ias" +
                        File.separator +
                        "vod",
                "vod_journal.bin"
        );
        vodCacheJournal = new VODCacheJournal();
        vodCacheJournal.initCacheJournal(file);
    }

    void initializeCaches() {
        File cacheDir = context.getFilesDir();
        switch (cacheSize) {
            case CacheSize.SMALL:
                fastCacheSize = MB_5;
                commonCacheSize = MB_10;
                break;
            case CacheSize.LARGE:
                commonCacheSize = MB_200;
                break;
        }
        String prefix = File.separator + "ias" + File.separator;
        fastCache = new LruDiskCache(
                cacheDir,
                prefix + "fastCache",
                fastCacheSize,
                CacheType.FAST
        );
        commonCache = new LruDiskCache(
                cacheDir,
                prefix + "commonCache",
                commonCacheSize,
                CacheType.COMMON
        );
        infiniteCache = new LruDiskCache(
                cacheDir,
                prefix + "infiniteCache",
                cacheDir.getFreeSpace(),
                CacheType.INFINITE
        );
        bundleCache = new LruDiskCache(
                cacheDir,
                prefix + "bundleCache",
                cacheDir.getFreeSpace(),
                CacheType.BUNDLE
        );
        vodCache = new LruDiskCache(
                cacheDir,
                prefix + "vodCache",
                MB_500,
                CacheType.VOD
        );
    }

    void scheduleReinit() {
        if (!isReinitializing.compareAndSet(false, true)) {
            return;
        }

        initExecutor.submit(() -> {
            try {
                initializeCaches();
            } finally {
                isReinitializing.set(false);
            }
        });
    }


    LruDiskCache getCacheOrReinit(LruDiskCache cacheToCheck) {
        if (cacheToCheck == dummyCache) {
            scheduleReinit();
        }
        return cacheToCheck;
    }


    public LruDiskCache getCommonCache() {
        return getCacheOrReinit(commonCache);
    }

    @Override
    public LruDiskCache getFastCache() {
        return getCacheOrReinit(fastCache);
    }

    @Override
    public LruDiskCache getVodCache() {
        return getCacheOrReinit(vodCache);
    }

    @Override
    public LruDiskCache getBundleCache() {
        return getCacheOrReinit(bundleCache);
    }

    @Override
    public LruDiskCache getInfiniteCache() {
        return getCacheOrReinit(infiniteCache);
    }

    @Override
    public VODCacheJournal getVodCacheJournal() {
        return vodCacheJournal;
    }

    @Override
    public void setCacheSizes(long freeSpace) {
        switch (cacheSize) {
            case CacheSize.SMALL:
                fastCacheSize = MB_5;
                commonCacheSize = MB_10;
                break;
            case CacheSize.LARGE:
                commonCacheSize = MB_200;
                break;
        }
        if (freeSpace < commonCacheSize + fastCacheSize + MB_10) {
            commonCacheSize = MB_50;
            if (freeSpace < commonCacheSize + fastCacheSize + MB_10) {
                commonCacheSize = MB_10;
                fastCacheSize = MB_5;
                if (freeSpace < commonCacheSize + fastCacheSize + MB_10) {
                    getFastCache().setCacheSize(MB_1);
                    getCommonCache().setCacheSize(MB_1);
                    InAppStoryManager.showELog(
                            LoggerTags.IAS_ERROR_TAG,
                            StringsUtils.getErrorStringFromContext(
                                    context,
                                    R.string.ias_min_free_space_error
                            )
                    );
                    return;
                }
            }
        }
        getFastCache().setCacheSize(fastCacheSize);
        getCommonCache().setCacheSize(commonCacheSize);
    }

    @Override
    public void clear() {
        try {
            fastCache.clearCache();
        } catch (Exception e) {

        }
        try {
            commonCache.clearCache();
        } catch (Exception e) {

        }
        try {
            infiniteCache.clearCache();
        } catch (Exception e) {

        }
        try {
            vodCache.clearCache();
        } catch (Exception e) {

        }
        vodCacheJournal.clear();
    }

    void runFreeSpaceCheck() {
        checkSpaceThread.scheduleAtFixedRate(checkFreeSpace, 1L, 60000L, TimeUnit.MILLISECONDS);
    }

    Runnable checkFreeSpace = new Runnable() {
        @Override
        public void run() {
            if (isReinitializing.get()) return;
            LruDiskCache commonCache = getCommonCache();
            if (dummyCache != commonCache)
                setCacheSizes(commonCache.getCacheDir().getFreeSpace());
        }
    };
}
