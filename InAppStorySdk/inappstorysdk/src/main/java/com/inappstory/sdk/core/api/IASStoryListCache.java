package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.api.models.Story;

public interface IASStoryListCache {
    void clearCachedLists();
    String getLocalOpensKey(Story.StoryType type);
}
