package com.inappstory.sdk.stories.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadThreadsHolder {
    private final ExecutorService fastCacheFileDownloader = Executors.newFixedThreadPool(1);

    public void useFastDownloader(Runnable runnable) {
        fastCacheFileDownloader.submit(runnable);
    }
}
