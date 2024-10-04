package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;

import java.util.List;

public interface IASStatistic {
    void createV1(String sessionId, boolean disabled);
    void removeV1(String sessionId);
    IASStatisticV1 v1();
    void v1(String sessionId, GetStatisticV1Callback callback);
    void v1(GetStatisticV1Callback callback);
    IASStatisticV2 v2();
    IASStatisticProfiling profiling();
    IASStatisticExceptions exceptions();

    void addViewedId(int id);
    boolean hasViewedId(int id);
    boolean hasViewedIds();
    void clearViewedIds();
    List<Integer> newStatisticPreviews(List<Integer> vals);
}
