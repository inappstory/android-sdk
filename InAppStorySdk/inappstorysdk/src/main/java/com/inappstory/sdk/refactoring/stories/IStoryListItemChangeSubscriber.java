package com.inappstory.sdk.refactoring.stories;

import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;

public interface IStoryListItemChangeSubscriber {
    void onChange(StoryListItemDTO storyDTO);

    String getStoryId();
}
