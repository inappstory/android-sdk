package com.inappstory.sdk.refactoring.stories;

import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;

public interface IStoryChangeSubscriber {
    void onChange(StoryDTO storyDTO);

    String getStoryId();
}
