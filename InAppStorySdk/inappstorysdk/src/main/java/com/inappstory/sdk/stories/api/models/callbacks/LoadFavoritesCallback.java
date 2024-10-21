package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.stories.ui.list.StoryFavoriteImage;

import java.util.List;

public interface LoadFavoritesCallback {
    void success(List<StoryFavoriteImage> favoriteImages);
}
