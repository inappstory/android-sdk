package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruCachesHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;

public class SessionBundleResourceUseCase extends GetCacheFileUseCase<Void> {
    public SessionBundleResourceUseCase(FilesDownloadManager filesDownloadManager) {
        super(filesDownloadManager);
    }

    @Override
    public Void getFile() {
        return null;
    }

    @Override
    protected CacheJournalItem generateCacheItem() {
        return null;
    }

    @Override
    protected LruDiskCache getCache() {
        return filesDownloadManager.getCachesHolder().getInfiniteCache();
    }
}
