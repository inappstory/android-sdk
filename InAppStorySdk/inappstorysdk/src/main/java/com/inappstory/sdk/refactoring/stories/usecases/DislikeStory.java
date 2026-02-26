package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.core.utils.models.UseCaseWithSession;
import com.inappstory.sdk.refactoring.session.repositories.ISessionRepository;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class DislikeStory extends UseCaseWithSession<Boolean> {
    private final IStoryRepository storyRepository;
    private final String storyId;
    private final boolean dislike;

    public DislikeStory(
            IStoryRepository storyRepository,
            ISessionRepository sessionRepository,
            String storyId,
            boolean dislike
    ) {
        super(sessionRepository);
        this.storyRepository = storyRepository;
        this.storyId = storyId;
        this.dislike = dislike;
    }

    @Override
    protected void invokeWithSession(ResultCallback<Boolean> callback) {
        this.storyRepository.dislikeStory(storyId, dislike, callback);
    }
}
