package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class LikeStory {
    private final IStoryRepository storyRepository;

    public LikeStory(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    void invoke(String storyId, boolean like, ResultCallback<Boolean> callback) {
        this.storyRepository.likeStory(storyId, like, callback);
    }
}
