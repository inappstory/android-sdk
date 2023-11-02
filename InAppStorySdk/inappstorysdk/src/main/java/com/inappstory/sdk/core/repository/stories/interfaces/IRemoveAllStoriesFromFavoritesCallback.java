package com.inappstory.sdk.core.repository.stories.interfaces;

import com.inappstory.sdk.core.repository.session.interfaces.NetworkErrorCallback;

public interface IRemoveAllStoriesFromFavoritesCallback extends NetworkErrorCallback {
    void onRemove();
}
