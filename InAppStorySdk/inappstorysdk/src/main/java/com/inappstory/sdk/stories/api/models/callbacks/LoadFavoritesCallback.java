package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.stories.ui.list.FavoriteImage;

import java.util.List;

public interface LoadFavoritesCallback {
    void success(List<FavoriteImage> favoriteImages);
}
