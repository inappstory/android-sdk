package com.inappstory.sdk.lrudiskcache;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_200;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_500;

import android.content.Context;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;

import java.io.File;
import java.io.IOException;

public class LruCachesHolder {
    private LruDiskCache fastCache;
    private LruDiskCache commonCache;
    private LruDiskCache infiniteCache;
    private LruDiskCache bundleCache;
    private LruDiskCache vodCache;

    public LruCachesHolder(
            IASCore core,
            Context context,
            int cacheSize
    ) {
        try {
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
            this.fastCache = new LruDiskCache(
                    core,
                    cacheDir,
                    prefix + "fastCache",
                    fastCacheSize,
                    CacheType.FAST
            );
            this.commonCache = new LruDiskCache(
                    core,
                    cacheDir,
                    prefix + "commonCache",
                    commonCacheSize,
                    CacheType.COMMON
            );
            this.infiniteCache = new LruDiskCache(
                    core,
                    cacheDir,
                    prefix + "infiniteCache",
                    cacheDir.getFreeSpace(),
                    CacheType.INFINITE
            );
            this.bundleCache = new LruDiskCache(
                    core,
                    cacheDir,
                    prefix + "infiniteCache",
                    cacheDir.getFreeSpace(),
                    CacheType.BUNDLE
            );
            this.vodCache = new LruDiskCache(
                    core,
                    cacheDir,
                    prefix + "vodCache",
                    MB_500,
                    CacheType.VOD
            );
        } catch (IOException e) {
            core.exceptionManager().createExceptionLog(e);
        }
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
