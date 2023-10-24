package com.inappstory.sdk.stories.cache;

public interface IDownloadPageFile {
    void downloadFile(
            UrlWithAlter url,
            IDownloadPageFileCallback pageFileCallback
    );

    void onError(StoryTaskData task);

    void onSlideError(SlideTaskData slideTaskData);
}
