package com.inappstory.sdk.stories.ui.views;

import android.view.View;

import java.util.List;

import com.inappstory.sdk.stories.ui.list.FavoriteImage;

public interface IGetFavoriteListItem {
    View getFavoriteItem(List<FavoriteImage> favoriteImages, int count);
    void bindFavoriteItem(View favCell, List<FavoriteImage> favoriteImages, int count);
}
