package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

import java.util.List;

public class GetFavoriteStories {
    private final IStoryRepository storyRepository;

    public GetFavoriteStories(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public void invoke(ResultCallback<List<StoriesListItemDTO>> callback) {
        this.storyRepository.getFavoriteStories(callback);
    }
}
