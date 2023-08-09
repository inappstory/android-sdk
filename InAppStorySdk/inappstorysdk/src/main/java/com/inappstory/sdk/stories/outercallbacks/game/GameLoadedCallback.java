package com.inappstory.sdk.stories.outercallbacks.game;

import com.inappstory.sdk.stories.api.models.GameCenterData;

public interface GameLoadedCallback {
    public void complete(GameCenterData data, String error);
}
