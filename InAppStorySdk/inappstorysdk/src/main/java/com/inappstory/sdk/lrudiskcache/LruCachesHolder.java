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
    private LruDiskCache fastCache = dummyCache;
    private LruDiskCache commonCache = dummyCache;
    private LruDiskCache infiniteCache = dummyCache;
    private LruDiskCache bundleCache = dummyCache;
    private LruDiskCache vodCache = dummyCache;


    private final ExecutorService initExecutor = Executors.newFixedThreadPool(1);

    public LruCachesHolder(
            final IASCore core,
            final Context context,
            final int cacheSize
    ) {
        initExecutor.submit(new Runnable() {
            @Override
            public void run() {
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
                        prefix + "infiniteCache",
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
        });

    }

    public LruDiskCache getCommonCache() {
        return commonCache;
    }

    public LruDiskCache getFastCache() {
        return fastCache;
    }

    public LruDiskCache getVodCache() {
        return vodCache;
    }

    public LruDiskCache getBundleCache() {
        return bundleCache;
    }

    public LruDiskCache getInfiniteCache() {
        return infiniteCache;
    }

    public void clear() throws IOException {
        fastCache.clearCache();
        commonCache.clearCache();
        infiniteCache.clearCache();
        vodCache.clearCache();
        bundleCache.clearCache();
    }
}
