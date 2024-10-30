package com.inappstory.sdk.core.network.content.callbacks;

import com.inappstory.sdk.core.dataholders.models.IFavoriteItem;

import java.util.List;

public interface LoadFavoritesCallback {
    void success(List<IFavoriteItem> favoriteImages);
}
