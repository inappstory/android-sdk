package com.inappstory.sdk.stories.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadThreadsHolder {
    private final ExecutorService fastCacheFileDownloader = Executors.newFixedThreadPool(1);
    private final ExecutorService customDownloader = Executors.newFixedThreadPool(1);

    public void useFastCacheDownloader(Runnable runnable) {
        fastCacheFileDownloader.submit(runnable);
    }
    public void useCustomDownloader(Runnable runnable) {
        customDownloader.submit(runnable);
    }
}
