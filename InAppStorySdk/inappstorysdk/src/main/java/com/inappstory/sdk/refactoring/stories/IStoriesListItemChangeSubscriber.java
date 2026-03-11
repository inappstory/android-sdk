package com.inappstory.sdk.refactoring.stories;

import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;

public interface IStoriesListItemChangeSubscriber {
    void onChange(StoriesListItemDTO storyDTO);

    String getStoryId();
}
