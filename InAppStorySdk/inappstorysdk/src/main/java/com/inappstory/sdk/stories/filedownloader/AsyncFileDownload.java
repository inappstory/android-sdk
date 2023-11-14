package com.inappstory.sdk.stories.filedownloader;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.utils.lrudiskcache.LruDiskCache;

import java.util.concurrent.ExecutorService;

public abstract class AsyncFileDownload extends FileDownload {

    private ExecutorService service;

    public AsyncFileDownload(
            @NonNull String url,
            @NonNull LruDiskCache cache,
            @NonNull ExecutorService service
    ) {
        super(url, cache);
        this.service = service;
    }

    public AsyncFileDownload(
            @NonNull String url,
            Long checkSize,
            String checkSha1,
            Long needSpace,
            @NonNull LruDiskCache cache,
            @NonNull ExecutorService service
    ) {
        super(url, checkSize, checkSha1, needSpace, cache);
        this.service = service;
    }

    @Override
    public void downloadOrGetFromCache() throws Exception {
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    AsyncFileDownload.super.downloadOrGetFromCache();
                } catch (Exception ignored) {

                }
            }
        });
    }
}
