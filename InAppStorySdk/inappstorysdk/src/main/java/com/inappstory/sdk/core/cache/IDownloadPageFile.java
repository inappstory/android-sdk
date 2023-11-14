package com.inappstory.sdk.core.cache;

public interface IDownloadPageFile {
    void downloadFile(
            UrlWithAlter url,
            IDownloadPageFileCallback pageFileCallback
    );

    void onError(StoryTaskData task);

    void onSlideError(SlideTaskData slideTaskData);
}
