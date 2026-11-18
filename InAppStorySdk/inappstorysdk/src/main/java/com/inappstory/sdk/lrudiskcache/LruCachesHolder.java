package com.inappstory.sdk.lrudiskcache;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_200;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_500;

import android.content.Context;
import android.os.Handler;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LruCachesHolder {
    private final LruDiskCache dummyCache = new DummyLruDiskCache();
    private volatile LruDiskCache fastCache = dummyCache;
    private volatile LruDiskCache commonCache = dummyCache;
    private volatile LruDiskCache infiniteCache = dummyCache;
    private volatile LruDiskCache bundleCache = dummyCache;
    private volatile LruDiskCache vodCache = dummyCache;
    private final IASCore core;
    private final Context context;
    private final int cacheSize;

    private final ExecutorService initExecutor = Executors.newFixedThreadPool(1);

    public LruCachesHolder(
            final IASCore core,
            final Context context,
            final int cacheSize
    ) {
        this.core = core;
        this.context = context.getApplicationContext();
        this.cacheSize = cacheSize;
        initExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    initializeCaches();
                } catch (Exception ignored) {

                }
            }
        });
    }

    private void initializeCaches() {
        File cacheDir = context.getFilesDir();
        long commonCacheSize = MB_100;
        long fastCacheSize = MB_10;
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
                core,
                cacheDir,
                prefix + "fastCache",
                fastCacheSize,
                CacheType.FAST
        );
        commonCache = new LruDiskCache(
                core,
                cacheDir,
                prefix + "commonCache",
                commonCacheSize,
                CacheType.COMMON
        );
        infiniteCache = new LruDiskCache(
                core,
                cacheDir,
                prefix + "infiniteCache",
                cacheDir.getFreeSpace(),
                CacheType.INFINITE
        );
        bundleCache = new LruDiskCache(
                core,
                cacheDir,
                prefix + "bundleCache",
                cacheDir.getFreeSpace(),
                CacheType.BUNDLE
        );
        vodCache = new LruDiskCache(
                core,
                cacheDir,
                prefix + "vodCache",
                MB_500,
                CacheType.VOD
        );
    }

    private volatile boolean isReinitializing = false;

    private void scheduleReinit() {
        if (isReinitializing) return;
        isReinitializing = true;

        initExecutor.submit(() -> {
            try {
                initializeCaches();
            } finally {
                isReinitializing = false;
            }
        });
    }


    public LruDiskCache getCacheOrReinit(LruDiskCache cacheToCheck) {
        if (cacheToCheck == dummyCache || cacheToCheck == null) {
            scheduleReinit();
        }
        return cacheToCheck != null ? cacheToCheck : dummyCache;
    }


    public LruDiskCache getCommonCache() {
        return getCacheOrReinit(commonCache);
    }

    public LruDiskCache getFastCache() {
        return getCacheOrReinit(fastCache);
    }

    public LruDiskCache getVodCache() {
        return getCacheOrReinit(vodCache);
    }

    public LruDiskCache getBundleCache() {
        return getCacheOrReinit(bundleCache);
    }

    public LruDiskCache getInfiniteCache() {
        return getCacheOrReinit(infiniteCache);
    }

    public void clear() throws IOException {
        fastCache.clearCache();
        commonCache.clearCache();
        infiniteCache.clearCache();
        vodCache.clearCache();
    }
}
