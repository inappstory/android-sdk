package com.inappstory.sdk.stories.ui.list;

import java.util.List;

public interface IFavoriteCellUpdate {
    void update(List<FavoriteImage> images, boolean isEmpty);

    List<FavoriteImage> getFavoriteImages();
}
