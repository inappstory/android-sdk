package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.session.usecases.UseCaseWithSession;
import com.inappstory.sdk.refactoring.session.repositories.ISessionRepository;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class FavoriteStory extends UseCaseWithSession<Boolean> {
    private final IStoryRepository storyRepository;
    private final String storyId;
    private final boolean favorite;

    public FavoriteStory(
            IStoryRepository storyRepository,
            ISessionRepository sessionRepository,
            String storyId,
            boolean favorite
    ) {
        super(sessionRepository);
        this.storyRepository = storyRepository;
        this.storyId = storyId;
        this.favorite = favorite;
    }

    @Override
    protected void invokeWithSession(ResultCallback<Boolean> callback) {
        this.storyRepository.favoriteStory(storyId, favorite, callback);
    }
}
