package com.inappstory.sdk.utils;

import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.SessionAsset;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;

import java.util.List;

public interface ISessionHolder {
    boolean allowUGC();
    String getSessionId();
    List<SessionAsset> getSessionAssets();
    void setSession(Session session, boolean v1Disabled);
    void sessionData(CachedSessionData sessionData);

    void addSessionAssetsKeys(List<SessionAsset> cacheObjects);
    void addSessionAsset(SessionAsset cacheObject);

    void addSessionAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void removeSessionAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    boolean checkIfSessionAssetsIsReadySync();
    void assetsIsCleared();
    boolean checkIfSessionAssetsIsReadyAsync();

    void clear(String oldSessionId);
    CachedSessionData sessionData();
}
