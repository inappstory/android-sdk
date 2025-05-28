package com.inappstory.sdk.stories.outercallbacks.game;

import com.inappstory.sdk.stories.api.models.GameCenterData;

public interface GameLoadedError {
    void onError(GameCenterData data, String error);
}
