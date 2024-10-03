package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.api.models.Story;

public interface IASStoriesOpenedCache {
    String getLocalOpensKey(Story.StoryType type);
    void clearLocalOpensKey();
}
