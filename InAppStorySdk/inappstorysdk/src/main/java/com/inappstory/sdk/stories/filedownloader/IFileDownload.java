package com.inappstory.sdk.stories.filedownloader;

import com.inappstory.sdk.stories.cache.DownloadFileState;

public interface IFileDownload extends
        ICacheSettings,
        ILruCacheHolder,
        IInterruptionHolder,
        IFileDownloadProgressCallback {
    DownloadFileState downloadOrGetFromCache() throws Exception;
}
