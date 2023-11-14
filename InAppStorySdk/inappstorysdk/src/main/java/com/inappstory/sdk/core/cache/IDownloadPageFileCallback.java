package com.inappstory.sdk.core.cache;

public interface IDownloadPageFileCallback {
    void download(
            UrlWithAlter source,
            DownloadPageFileStatus status
    );
}
