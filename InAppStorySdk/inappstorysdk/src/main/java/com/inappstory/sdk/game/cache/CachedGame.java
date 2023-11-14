package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.core.models.api.GameCenterData;

public class CachedGame {
    public CachedGame(GameCenterData data) {
        this.data = data;
    }

    public CachedGame() {
    }

    public GameCenterData getData() {
        return data;
    }

    public void setData(GameCenterData data) {
        this.data = data;
    }

    public boolean isSplashCached() {
        return isSplashCached;
    }

    public void setSplashCached(boolean splashCached) {
        isSplashCached = splashCached;
    }

    public boolean isResourcesCached() {
        return isResourcesCached;
    }

    public void setResourcesCached(boolean resourcesCached) {
        isResourcesCached = resourcesCached;
    }

    GameCenterData data;
    boolean isSplashCached;
    boolean isResourcesCached;
}
