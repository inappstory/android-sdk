package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruCachesHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;


public abstract class GetCacheFileUseCase<T> {
    protected final IASCore core;
    protected String uniqueKey;
    protected String filePath;
    protected GenerateDownloadLog downloadLog = new GenerateDownloadLog();

    public GetCacheFileUseCase(IASCore core) {
        this.core = core;
    }

    abstract public T getFile();

    abstract protected CacheJournalItem generateCacheItem();

    abstract protected LruDiskCache getCache();
}
