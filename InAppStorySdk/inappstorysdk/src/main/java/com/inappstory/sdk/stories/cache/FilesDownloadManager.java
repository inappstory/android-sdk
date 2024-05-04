package com.inappstory.sdk.stories.cache;

import android.content.Context;

import com.inappstory.sdk.lrudiskcache.LruCachesHolder;

public class FilesDownloadManager {

    public LruCachesHolder getCachesHolder() {
        return cachesHolder;
    }

    private final LruCachesHolder cachesHolder;
    private final DownloadThreadsHolder downloadThreadsHolder;

    public FilesDownloadManager(Context context, int cacheSize) {
        cachesHolder = new LruCachesHolder(context, cacheSize);
        downloadThreadsHolder  = new DownloadThreadsHolder();
    }

    public void useFastDownloader(Runnable runnable) {
        downloadThreadsHolder.useFastDownloader(runnable);
    }
}
