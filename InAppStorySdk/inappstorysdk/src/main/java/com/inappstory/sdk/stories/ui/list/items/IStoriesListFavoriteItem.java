package com.inappstory.sdk.stories.ui.list.items;

import com.inappstory.sdk.stories.ui.list.FavoriteImage;

import java.util.List;

public interface IStoriesListFavoriteItem {
    void bindFavorite(List<FavoriteImage> favoriteImages);

    void setImages(List<FavoriteImage> favoriteImages);
}
