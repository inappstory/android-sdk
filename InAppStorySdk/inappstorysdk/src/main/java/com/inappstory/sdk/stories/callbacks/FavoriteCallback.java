package com.inappstory.sdk.stories.callbacks;

public interface FavoriteCallback {
    void addedToFavorite();
    void removedFromFavorite();

    void onError();
}
