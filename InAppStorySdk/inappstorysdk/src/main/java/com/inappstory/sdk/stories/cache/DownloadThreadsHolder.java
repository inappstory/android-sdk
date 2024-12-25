package com.inappstory.sdk.stories.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadThreadsHolder {
    private final ExecutorService fastCacheFileDownloader = Executors.newFixedThreadPool(1);
    private final ExecutorService bundleDownloader = Executors.newFixedThreadPool(5);

    public void useFastCacheDownloader(Runnable runnable) {
        fastCacheFileDownloader.submit(runnable);
    }
    public void useBundleDownloader(Runnable runnable) {
        bundleDownloader.submit(runnable);
    }
}
