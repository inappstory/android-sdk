package com.inappstory.sdk.utils;

import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.core.network.content.models.SessionAsset;

import java.util.List;

public interface ISessionHolder {
    boolean allowUGC();
    String getSessionId();
    List<SessionAsset> getSessionAssets();
    void setSession(CachedSessionData sessionData, boolean v1Disabled);
    void clear(String oldSessionId);
    CachedSessionData sessionData();
}
