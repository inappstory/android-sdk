package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

public class FavoriteStory {
    private final IStoryRepository storyRepository;

    public FavoriteStory(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    void invoke(String storyId, boolean favorite, ResultCallback<Boolean> callback) {
        this.storyRepository.favoriteStory(storyId, favorite, callback);
    }
}
