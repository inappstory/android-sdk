package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.stories.ui.list.FavoriteImage;

import java.util.ArrayList;
import java.util.List;

public class ExceptionCache {
    public List<Story> getStories() {
        List<Story> nonNullStories = new ArrayList<>();
        if (stories == null) return nonNullStories;
        for (Story story: stories) {
            if (story != null) nonNullStories.add(story);
        }
        return nonNullStories;
    }

    public List<Story> getFavStories() {
        List<Story> nonNullStories = new ArrayList<>();
        if (favStories == null) return nonNullStories;
        for (Story story: favStories) {
            if (story != null) nonNullStories.add(story);
        }
        return nonNullStories;
    }

    public List<FavoriteImage> getFavoriteImages() {
        List<FavoriteImage> nonNullFavoriteImages = new ArrayList<>();
        if (favoriteImages == null) return nonNullFavoriteImages;
        for (FavoriteImage favoriteImage: favoriteImages) {
            if (favoriteImage != null) nonNullFavoriteImages.add(favoriteImage);
        }
        return nonNullFavoriteImages;
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