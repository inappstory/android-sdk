package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.session.repositories.ISessionRepository;
import com.inappstory.sdk.refactoring.session.usecases.UseCaseWithSession;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class GetStoriesFeed extends UseCaseWithSession<StoryFeedDTO> {
    private final IStoryRepository storyRepository;
    private final StoriesFeedParameters feedParameters;

    public GetStoriesFeed(
            ISessionRepository sessionRepository,
            IStoryRepository storyRepository,
            StoriesFeedParameters feedParameters
    ) {
        super(sessionRepository);
        this.storyRepository = storyRepository;
        this.feedParameters = feedParameters;
    }

    @Override
    protected void invokeWithSession(ResultCallback<StoryFeedDTO> callback) {
        this.storyRepository.getStoriesFeed(feedParameters, true, callback);
    }
}
