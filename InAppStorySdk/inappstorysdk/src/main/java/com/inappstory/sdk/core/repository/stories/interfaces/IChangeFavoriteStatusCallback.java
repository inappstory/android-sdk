package com.inappstory.sdk.core.repository.stories.interfaces;


import com.inappstory.sdk.core.repository.session.interfaces.NetworkErrorCallback;

public interface IChangeFavoriteStatusCallback extends NetworkErrorCallback {
    void addedToFavorite();

    void removedFromFavorite();
}
