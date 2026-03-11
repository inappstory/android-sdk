package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class GetOnboardingStoriesFeed {
    private final IStoryRepository storyRepository;

    public GetOnboardingStoriesFeed(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    void invoke(StoriesFeedParameters feedParameters, int limit, ResultCallback<StoryFeedDTO> callback) {
        this.storyRepository.getOnboardingStoriesFeed(feedParameters, limit, callback);
    }
}
