package com.inappstory.sdk.packages.core.base.lrucache.models;

public interface ILruDiskCacheJournalItem {
    long lastUsedTime();
    void updateLastUsedTime();
    ILruCacheKey key();
    String sha1();
    String filePath();
    long downloadedSize();
    long fullSize();
}
