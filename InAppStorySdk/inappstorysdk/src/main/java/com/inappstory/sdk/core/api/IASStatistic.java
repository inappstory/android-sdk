package com.inappstory.sdk.core.api;

public interface IASStatistic {
    IASStatisticV1 v1();
    IASStatisticV2 v2();
    IASStatisticProfiling profiling();
    IASStatisticExceptions exceptions();
}
