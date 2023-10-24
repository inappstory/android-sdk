package com.inappstory.sdk.stories.cache;

public interface IDownloadPageFileCallback {
    void download(
            UrlWithAlter source,
            DownloadPageFileStatus status
    );
}
