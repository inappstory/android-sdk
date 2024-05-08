package com.inappstory.sdk.utils;

import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.SessionAsset;
import com.inappstory.sdk.stories.api.models.StatisticPermissions;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;

import java.util.List;

public interface ISessionHolder {
    boolean allowStatV1();
    boolean allowStatV2();
    boolean allowProfiling();
    boolean allowCrash();
    boolean allowUGC();
    String getSessionId();
    void setSessionPermissions(StatisticPermissions statisticPermissions);
    void setSession(Session session);

    void addViewedId(int id);
    boolean hasViewedId(int id);
    boolean hasViewedIds();

    void addSessionAssetsKeys(List<SessionAsset> cacheObjects);
    void addSessionAsset(SessionAsset cacheObject);

    void addSessionAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void removeSessionAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);

    void checkIfSessionAssetsIsReady(FilesDownloadManager filesDownloadManager);

    OldStatisticManager currentStatisticManager();

    OldStatisticManager getStatisticManager(String sessionId);

    void clear(String oldSessionId);
}
