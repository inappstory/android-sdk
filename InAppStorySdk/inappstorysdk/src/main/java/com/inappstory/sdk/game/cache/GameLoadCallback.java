package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.core.models.api.GameCenterData;

public interface GameLoadCallback {
    void onSuccess(GameCenterData data);
    void onError(String message);
}
