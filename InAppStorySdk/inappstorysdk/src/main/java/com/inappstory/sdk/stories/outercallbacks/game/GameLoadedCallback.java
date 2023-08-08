package com.inappstory.sdk.stories.outercallbacks.game;

import com.inappstory.sdk.stories.api.models.GameCenterData;

public interface GameLoadedCallback {
    public void complete(boolean success, GameCenterData data);
}
