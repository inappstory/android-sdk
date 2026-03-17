package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.session.repositories.ISessionRepository;
import com.inappstory.sdk.refactoring.session.usecases.UseCaseWithSession;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

import java.util.List;

public class GetFavoriteCovers extends UseCaseWithSession<List<StoryCoverDTO>> {
    private final IStoryRepository storyRepository;

    public GetFavoriteCovers(
            ISessionRepository sessionRepository,
            IStoryRepository storyRepository
    ) {
        super(sessionRepository);
        this.storyRepository = storyRepository;
    }

    @Override
    protected void invokeWithSession(ResultCallback<List<StoryCoverDTO>> callback) {
        this.storyRepository.getFavoriteCovers(callback);
    }

}
