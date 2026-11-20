package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;


public abstract class GetCacheFileUseCase<T> {
    protected final IASCore core;
    protected String uniqueKey;
    protected String filePath;
    protected GenerateDownloadLog downloadLog = new GenerateDownloadLog();

    public GetCacheFileUseCase(IASCore core) {
        this.core = core;
    }

    abstract public T getFile();

    protected String journalItemExt() {
        return null;
    }

    protected String journalItemType() {
        return null;
    }

    protected String journalItemSha1() {
        return null;
    }

    protected String journalItemReplaceKey() {
        return null;
    }

    protected String journalItemFilePath() {
        return filePath;
    }

    protected String journalItemKey() {
        return uniqueKey;
    }


    protected String journalItemMimeType() {
        return null;
    }



    protected CacheJournalItem generateCacheItem(long size, long downloadedSize) {
        return new CacheJournalItem(
                journalItemKey(),
                journalItemFilePath(),
                journalItemExt(),
                journalItemType(),
                journalItemSha1(),
                journalItemReplaceKey(),
                System.currentTimeMillis(),
                size,
                downloadedSize,
                journalItemMimeType()
        );
    }

    abstract protected LruDiskCache getCache();
}
