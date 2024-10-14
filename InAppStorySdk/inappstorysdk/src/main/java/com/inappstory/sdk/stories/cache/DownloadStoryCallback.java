package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.Story;

public interface DownloadStoryCallback {
    void onDownload(Story story, int loadType, ContentType type);
    void onError(StoryTaskKey storyTaskKey);
}
