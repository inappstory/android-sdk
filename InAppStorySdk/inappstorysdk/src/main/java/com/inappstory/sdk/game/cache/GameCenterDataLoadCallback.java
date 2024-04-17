package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;

public interface GameCenterDataLoadCallback {
    void onSuccess(IGameCenterData data);
    void onError(String message);
}
