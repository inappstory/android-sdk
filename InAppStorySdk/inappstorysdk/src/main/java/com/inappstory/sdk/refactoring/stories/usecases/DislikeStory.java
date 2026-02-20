package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class DislikeStory {
    private final IStoryRepository storyRepository;

    public DislikeStory(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    void invoke(String storyId, boolean dislike, ResultCallback<Boolean> callback) {
        this.storyRepository.dislikeStory(storyId, dislike, callback);
    }
}
