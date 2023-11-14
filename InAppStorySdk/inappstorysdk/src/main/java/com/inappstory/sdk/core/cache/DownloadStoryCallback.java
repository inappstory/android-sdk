package com.inappstory.sdk.core.cache;

import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.models.api.Story;

public interface DownloadStoryCallback {
    void onDownload(IStoryDTO story, int loadType, Story.StoryType type);
    void onError(StoryTaskData storyTaskData);
}
