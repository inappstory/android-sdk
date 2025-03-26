package com.inappstory.sdk.core.data;

import java.util.Map;

public interface IStatData {
    int id();
    String statTitle();
    int slidesCount();
    Map<String, Object> ugcPayload();
}
