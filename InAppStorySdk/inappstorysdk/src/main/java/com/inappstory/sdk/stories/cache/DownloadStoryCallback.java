package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.stories.api.models.Story;

public interface DownloadStoryCallback {
    void onDownload(IStoryDTO story, int loadType, Story.StoryType type);
    void onError(StoryTaskData storyTaskData);
}
