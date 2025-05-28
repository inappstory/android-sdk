package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.stories.cache.DownloadFileState;

public interface FinishDownloadFileCallback {
    void finish(DownloadFileState state);
}
