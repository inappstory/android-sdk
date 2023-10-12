package com.inappstory.sdk.stories.callbacks;

import com.inappstory.sdk.stories.api.models.Story;

public interface FavoriteCallback {
    void addedToFavorite(Story story);
    void removedFromFavorite();

    void onError();
}
