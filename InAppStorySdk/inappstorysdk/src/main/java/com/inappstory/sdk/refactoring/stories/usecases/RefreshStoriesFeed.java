package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class RefreshStoriesFeed {
    private final IStoryRepository storyRepository;

    public RefreshStoriesFeed(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    void invoke(StoriesFeedParameters feedParameters, ResultCallback<StoryFeedDTO> callback) {
        this.storyRepository.getStoriesFeed(feedParameters, false, callback);
    }
}
