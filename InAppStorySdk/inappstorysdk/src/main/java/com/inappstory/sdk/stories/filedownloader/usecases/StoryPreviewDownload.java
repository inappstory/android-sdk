package com.inappstory.sdk.stories.filedownloader.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.filedownloader.AsyncFileDownload;

import java.util.concurrent.ExecutorService;

public final class StoryPreviewDownload extends AsyncFileDownload {

    public StoryPreviewDownload(
            @NonNull String url,
            @NonNull LruDiskCache cache,
            @NonNull ExecutorService service
    ) {
        super(url, cache, service);
    }

    @Override
    public String getCacheKey() {
        return url;
    }

    @Override
    public String getDownloadFilePath() {
        return cache.getFileFromKey(getCacheKey()).getAbsolutePath();
    }

    @Override
    public void onProgress(long currentProgress, long max) {

    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

}
