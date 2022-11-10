package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.stories.api.models.Story;

public interface DownloadStoryCallback {
    void onDownload(Story story, int loadType, Story.StoryType type);
    void onError(int storyId);
}
