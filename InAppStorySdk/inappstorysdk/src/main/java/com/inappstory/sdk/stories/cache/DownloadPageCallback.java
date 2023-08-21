package com.inappstory.sdk.stories.cache;

public interface DownloadPageCallback {
    DownloadPageFileStatus downloadFile(UrlWithAlter url, SlideTaskData slideTaskData);
    void onError(StoryTaskData task);
    void onSlideError(SlideTaskData slideTaskData);
}
