package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

import java.util.List;

public class GetFavoriteCovers {
    private final IStoryRepository storyRepository;

    public GetFavoriteCovers(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public void invoke(ResultCallback<List<StoryCoverDTO>> callback) {
        this.storyRepository.getFavoriteCovers(callback);
    }
}
