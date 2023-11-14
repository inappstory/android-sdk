package com.inappstory.sdk.core.models;

import com.inappstory.sdk.core.models.api.Story;
import com.inappstory.sdk.core.repository.stories.dto.IFavoritePreviewStoryDTO;

import java.util.ArrayList;
import java.util.List;

public class ExceptionCache {
    public List<Story> getStories() {
        return stories;
    }

    public List<Story> getFavStories() {
        return favStories;
    }

    public List<IFavoritePreviewStoryDTO> getFavoriteImages() {
        return favoriteImages;
    }

    private List<Story> stories;
    private List<Story> favStories;
    private List<IFavoritePreviewStoryDTO> favoriteImages;

    public ExceptionCache(List<Story> stories, List<Story> favStories, List<IFavoritePreviewStoryDTO> favoriteImages) {
        this.stories = stories != null ? new ArrayList<>(stories)
                : new ArrayList<Story>();
        this.favStories = favStories != null ? new ArrayList<>(favStories)
                : new ArrayList<Story>();
        this.favoriteImages = favoriteImages != null ? new ArrayList<>(favoriteImages)
                : new ArrayList<IFavoritePreviewStoryDTO>();
    }
}
