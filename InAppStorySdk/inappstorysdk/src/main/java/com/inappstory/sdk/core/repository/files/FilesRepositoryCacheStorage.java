package com.inappstory.sdk.core.repository.files;

import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_50;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.lrudiskcache.CacheType;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;

import java.io.File;
import java.io.IOException;

public class FilesRepositoryCacheStorage {


    private LruDiskCache fastCache;
    private LruDiskCache commonCache;
    private LruDiskCache infiniteCache;

    private File cacheDir;
    private final Object cacheLock = new Object();

    private final String IAS_PREFIX = File.separator + "ias" + File.separator;

    public FilesRepositoryCacheStorage(File cacheDir) {
        this.cacheDir = cacheDir;
    }

    LruDiskCache getFastCache() {
        synchronized (cacheLock) {
            if (fastCache == null) {
                try {
                    fastCache = LruDiskCache.create(
                            cacheDir,
                            IAS_PREFIX,
                            MB_10, CacheType.FAST
                    );
                } catch (IOException e) {
                    InAppStoryService.createExceptionLog(e);
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
                    InAppStoryService.createExceptionLog(e);
                }
            }
            return infiniteCache;
        }
    }

    LruDiskCache getCommonCache() {
        synchronized (cacheLock) {
            if (commonCache == null) {
                try {
                    long cacheType = MB_100;
                    long fastCacheType = MB_10;
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
                                cacheType, CacheType.COMMON
                        );
                    }
                } catch (IOException e) {
                    InAppStoryService.createExceptionLog(e);
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
