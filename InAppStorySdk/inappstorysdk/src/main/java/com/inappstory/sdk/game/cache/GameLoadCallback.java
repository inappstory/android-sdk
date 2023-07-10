package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.stories.api.models.GameCenterData;

public interface GameLoadCallback {
    void onSuccess(GameCenterData data);
    void onError();
}
