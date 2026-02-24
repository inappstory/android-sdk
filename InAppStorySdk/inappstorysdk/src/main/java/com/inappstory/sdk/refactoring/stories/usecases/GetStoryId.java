package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class GetStoryId {
    private final IStoryRepository storyRepository;

    public GetStoryId(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    void invoke(String storySlugOrId, ResultCallback<StoryDTO> callback) {
        this.storyRepository.getStoryBySlugOrId(storySlugOrId, false, callback);
    }
}
