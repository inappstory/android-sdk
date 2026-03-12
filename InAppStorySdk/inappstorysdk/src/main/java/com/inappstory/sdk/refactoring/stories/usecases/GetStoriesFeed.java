package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class GetStoriesFeed {
    private final IStoryRepository storyRepository;

    public GetStoriesFeed(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public void invoke(StoriesFeedParameters feedParameters, ResultCallback<StoryFeedDTO> callback) {
        this.storyRepository.getStoriesFeed(feedParameters, true, callback);
    }
}
