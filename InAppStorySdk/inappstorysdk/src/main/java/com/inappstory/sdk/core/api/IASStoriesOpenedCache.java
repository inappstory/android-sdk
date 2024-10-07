package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.api.models.Story;

import java.util.List;

public interface IASStoriesOpenedCache {
    String getLocalOpensKey(Story.StoryType type);

    void clearLocalOpensKey();

    void saveStoriesOpened(final List<Story> stories, final Story.StoryType type);

    void saveStoryOpened(final int id, final Story.StoryType type);
}
