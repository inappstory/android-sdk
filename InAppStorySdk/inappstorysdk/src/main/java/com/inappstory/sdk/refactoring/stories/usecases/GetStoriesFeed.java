package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class GetStoriesFeed {
    private final IStoryRepository storyRepository;

    public GetStoriesFeed(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    void invoke(StoryFeedParameters feedParameters, ResultCallback<StoryFeedDTO> callback) {
        this.storyRepository.getStoriesFeed(feedParameters, true, callback);
    }
}
