package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.stories.ui.list.FavoriteImage;

import java.util.ArrayList;
import java.util.List;

public class ExceptionCache {
    public List<Story> getStories() {
        return stories;
    }

    public List<Story> getFavStories() {
        return favStories;
    }

    public List<FavoriteImage> getFavoriteImages() {
        return favoriteImages;
    }

    private List<Story> stories;
    private List<Story> favStories;
    private List<FavoriteImage> favoriteImages;

    public ExceptionCache(List<Story> stories, List<Story> favStories, List<FavoriteImage> favoriteImages) {
        this.stories = stories != null ? new ArrayList<>(stories)
                : new ArrayList<Story>();
        this.favStories = favStories != null ? new ArrayList<>(favStories)
                : new ArrayList<Story>();
        this.favoriteImages = favoriteImages != null ? new ArrayList<>(favoriteImages)
                : new ArrayList<FavoriteImage>();
    }
}
