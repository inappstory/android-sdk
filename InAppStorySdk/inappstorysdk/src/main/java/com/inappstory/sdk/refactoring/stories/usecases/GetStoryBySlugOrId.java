package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class GetStoryBySlugOrId {
    private final IStoryRepository storyRepository;

    public GetStoryBySlugOrId(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    void invoke(String storySlugOrId, boolean once, ResultCallback<StoryDTO> callback) {
        this.storyRepository.getStoryBySlugOrId(storySlugOrId, once, callback);
    }
}
