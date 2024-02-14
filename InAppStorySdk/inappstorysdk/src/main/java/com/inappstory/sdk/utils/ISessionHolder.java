package com.inappstory.sdk.utils;

import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.StatisticPermissions;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;

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

    OldStatisticManager currentStatisticManager();

    OldStatisticManager getStatisticManager(String sessionId);

    void clear(String oldSessionId);
}
