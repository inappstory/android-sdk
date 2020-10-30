package io.casestory.sdk.stories.ui.views;

import android.view.View;

import java.util.List;
import java.util.Map;

import io.casestory.sdk.stories.ui.list.FavoriteImage;
import io.casestory.sdk.stories.ui.views.StoriesListItem;

public interface IGetFavoriteListItem {
    View getFavoriteItem(List<FavoriteImage> favoriteImages);
    void bindFavoriteItem(View favCell, List<FavoriteImage> favoriteImages);
}
