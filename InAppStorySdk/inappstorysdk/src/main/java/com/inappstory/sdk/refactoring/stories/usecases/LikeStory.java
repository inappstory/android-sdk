package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.session.repositories.ISessionRepository;
import com.inappstory.sdk.refactoring.session.usecases.UseCaseWithSession;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class LikeStory extends UseCaseWithSession<Boolean> {
    private final IStoryRepository storyRepository;
    private final String storyId;
    private final boolean like;

    public LikeStory(
            IStoryRepository storyRepository,
            ISessionRepository sessionRepository,
            String storyId,
            boolean like
    ) {
        super(sessionRepository);
        this.storyRepository = storyRepository;
        this.storyId = storyId;
        this.like = like;
    }

    @Override
    protected void invokeWithSession(ResultCallback<Boolean> callback) {
        this.storyRepository.likeStory(storyId, like, callback);
    }
}
