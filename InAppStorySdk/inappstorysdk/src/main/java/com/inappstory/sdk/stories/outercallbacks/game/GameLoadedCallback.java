package com.inappstory.sdk.stories.outercallbacks.game;

import com.inappstory.sdk.core.models.api.GameCenterData;

public interface GameLoadedCallback {
    public void complete(GameCenterData data, String error);
}
