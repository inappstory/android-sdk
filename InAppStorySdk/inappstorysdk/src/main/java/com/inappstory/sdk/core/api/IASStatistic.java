package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;

import java.util.List;

public interface IASStatistic {
    void changeSession(CachedSessionData sessionData, boolean disabled);
    void clearSession(String sessionId);

    IASStatisticStoriesV1 storiesV1();
    IASStatisticStoriesV2 storiesV2();
    IASStatisticIAMV1 iamV1();
    IASStatisticBannerV1 bannersV1();
    void storiesV1(String sessionId, GetStatisticV1Callback callback);
    void storiesV1(GetStatisticV1Callback callback);
    IASStatisticProfiling profiling();
    IASStatisticExceptions exceptions();

    void addViewedId(int id);
    boolean hasViewedId(int id);
    boolean hasViewedIds();
    void clearViewedIds();
    List<Integer> newStatisticPreviews(List<Integer> vals);
}
