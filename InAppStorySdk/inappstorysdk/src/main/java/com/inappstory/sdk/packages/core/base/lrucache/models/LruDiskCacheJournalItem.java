package com.inappstory.sdk.packages.core.base.lrucache.models;

public class LruDiskCacheJournalItem implements ILruDiskCacheJournalItem {
    private ILruCacheKey key;
    private String filePath;
    private String sha1;
    private long lastUsedTime;
    private long downloadedSize;
    private long fullSize;


    public LruDiskCacheJournalItem(
            ILruCacheKey key,
            String filePath,
            String sha1,
            long downloadedSize,
            long fullSize
    ) {
        this.key = key;
        this.sha1 = sha1;
        this.filePath = filePath;
        this.downloadedSize = downloadedSize;
        this.fullSize = fullSize;
        this.lastUsedTime = System.currentTimeMillis();
    }

    public LruDiskCacheJournalItem(
            ILruCacheKey key,
            String filePath,
            long downloadedSize,
            long fullSize
    ) {
        this.key = key;
        this.filePath = filePath;
        this.downloadedSize = downloadedSize;
        this.fullSize = fullSize;
        this.lastUsedTime = System.currentTimeMillis();
    }

    @Override
    public long lastUsedTime() {
        return this.lastUsedTime;
    }

    @Override
    public void updateLastUsedTime() {
        this.lastUsedTime = System.currentTimeMillis();
    }

    @Override
    public ILruCacheKey key() {
        return key;
    }

    @Override
    public String sha1() {
        return sha1;
    }

    @Override
    public String filePath() {
        return filePath;
    }

    @Override
    public long downloadedSize() {
        return downloadedSize;
    }

    @Override
    public long fullSize() {
        return fullSize;
    }
}
