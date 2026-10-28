package com.inappstory.sdk.core.data;

import java.util.Map;

public interface IStatData {
    int id();
    String statTitle();
    Map<String, String> customAttributes();
    int slidesCount();
    Map<String, Object> ugcPayload();
}
