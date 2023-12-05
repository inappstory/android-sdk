package com.inappstory.sdk.stories.filedownloader.usecases;

import androidx.annotation.NonNull;


import com.inappstory.sdk.core.utils.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.filedownloader.FileDownload;

public final class StoryFileDownload extends FileDownload {
    public StoryFileDownload(
            String url,
            @NonNull LruDiskCache cache
    ) {
        super(url, cache);
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
