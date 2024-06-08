package com.inappstory.sdk.stories.cache;

public interface DownloadPageCallback {
    DownloadPageFileStatus downloadFile(
            UrlWithAlter url,
            SlideTaskData slideTaskData
    );

    DownloadPageFileStatus downloadVODFile(
            String url,
            String uniqueKey,
            SlideTaskData slideTaskData,
            long start,
            long end
    );

    void onError(StoryTaskData task);

    void onSlideError(SlideTaskData slideTaskData);
}
