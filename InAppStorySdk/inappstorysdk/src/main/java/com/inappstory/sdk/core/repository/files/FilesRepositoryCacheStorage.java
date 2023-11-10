package com.inappstory.sdk.core.repository.files;

import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_200;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_50;

import android.os.Handler;


import com.inappstory.sdk.core.lrudiskcache.CacheSize;
import com.inappstory.sdk.core.lrudiskcache.CacheType;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;

import java.io.File;
import java.io.IOException;

public class FilesRepositoryCacheStorage {
    private LruDiskCache fastCache;
    private LruDiskCache commonCache;
    private LruDiskCache infiniteCache;

    private final File cacheDir;
    private final Object cacheLock = new Object();

    private final String IAS_PREFIX = File.separator + "ias" + File.separator;

    long commonCacheSize = MB_100;
    long fastCacheSize = MB_10;

    public FilesRepositoryCacheStorage(File cacheDir, int cacheSizeType) {
        this.cacheDir = cacheDir;
        switch (cacheSizeType) {
            case CacheSize.SMALL:
                fastCacheSize = MB_5;
                commonCacheSize = MB_10;
                break;
            case CacheSize.LARGE:
                commonCacheSize = MB_200;
                break;
        }
        spaceHandler.postDelayed(checkFreeSpace, 60000);
    }

    Runnable checkFreeSpace = new Runnable() {
        @Override
        public void run() {
            LruDiskCache commonCache = getCommonCache();
            LruDiskCache fastCache = getFastCache();
            if (commonCache != null && fastCache != null) {
                long freeSpace = commonCache.getCacheDir().getFreeSpace();
                if (freeSpace < commonCache.getCacheSize() + fastCache.getCacheSize() + MB_10) {
                    commonCache.setCacheSize(MB_50);
                    if (freeSpace < commonCache.getCacheSize() + fastCache.getCacheSize() + MB_10) {
                        commonCache.setCacheSize(MB_10);
                        fastCache.setCacheSize(MB_5);
                        if (freeSpace < commonCache.getCacheSize() + fastCache.getCacheSize() + MB_10) {
                            commonCache.setCacheSize(MB_10);
                            fastCache.setCacheSize(MB_5);
                        }
                    }
                }
            }
            spaceHandler.postDelayed(checkFreeSpace, 60000);
        }
    };


    Handler spaceHandler = new Handler();

    LruDiskCache getFastCache() {
        synchronized (cacheLock) {
            if (fastCache == null) {
                try {
                    fastCache = LruDiskCache.create(
                            cacheDir,
                            IAS_PREFIX,
                            fastCacheSize,
                            CacheType.FAST
                    );
                } catch (IOException e) {
                }
            }
            return fastCache;
        }
    }

    LruDiskCache getInfiniteCache() {
        synchronized (cacheLock) {
            if (infiniteCache == null) {
                try {
                    long cacheType = cacheDir.getFreeSpace();
                    if (cacheType > 0) {
                        infiniteCache = LruDiskCache.create(
                                cacheDir,
                                IAS_PREFIX,
                                cacheType,
                                CacheType.INFINITE
                        );
                    }
                } catch (IOException e) {
                }
            }
            return infiniteCache;
        }
    }

    LruDiskCache getCommonCache() {
        synchronized (cacheLock) {
            if (commonCache == null) {
                try {
                    long cacheType = commonCacheSize;
                    long fastCacheType = fastCacheSize;
                    long freeSpace = cacheDir.getFreeSpace();
                    if (freeSpace < cacheType + fastCacheType + MB_10) {
                        cacheType = MB_50;
                        if (freeSpace < cacheType + fastCacheType + MB_10) {
                            cacheType = MB_10;
                            fastCacheType = MB_5;
                            if (freeSpace < cacheType + fastCacheType + MB_10) {
                                cacheType = 0;
                            }
                        }
                    }
                    if (cacheType > 0) {
                        commonCache = LruDiskCache.create(
                                cacheDir,
                                IAS_PREFIX,
                                cacheType,
                                CacheType.COMMON
                        );
                    }
                } catch (IOException e) {
                }
            }
            return commonCache;
        }
    }

    void clearCaches() {
        try {
            getCommonCache().clearCache();
            getFastCache().clearCache();
            getInfiniteCache().clearCache();
        } catch (IOException ignored) {

        }
    }
}
