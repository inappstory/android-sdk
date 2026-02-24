package com.inappstory.sdk.refactoring.stories.usecases;

import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;

import java.util.List;

public class GetFavoriteStories {
    private final IStoryRepository storyRepository;

    public GetFavoriteStories(IStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    void invoke(ResultCallback<List<StoryListItemDTO>> callback) {
        this.storyRepository.getFavoriteStories(callback);
    }
}
