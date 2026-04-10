package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.session.usecases.UseCaseWithSession;
import com.inappstory.sdk.refactoring.session.repositories.ISessionRepository;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class GetStoryById extends UseCaseWithSession<StoryDTO> {
    private final IStoryRepository storyRepository;
    private final String storySlugOrId;

    public GetStoryById(
            ISessionRepository sessionRepository,
            IStoryRepository storyRepository,
            String storySlugOrId
    ) {
        super(sessionRepository);
        this.storyRepository = storyRepository;
        this.storySlugOrId = storySlugOrId;
    }

    @Override
    protected void invokeWithSession(ResultCallback<StoryDTO> callback) {
        this.storyRepository.getStoryBySlugOrId(storySlugOrId, false, false, callback);
    }
}
