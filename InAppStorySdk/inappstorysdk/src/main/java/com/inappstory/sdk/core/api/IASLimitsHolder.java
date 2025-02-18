package com.inappstory.sdk.core.api;

import com.inappstory.sdk.core.data.IInAppMessageLimit;

import java.util.List;

public interface IASLimitsHolder {
    List<IInAppMessageLimit> cachedLimitForIds(String ids);
    void addLimitToCache(String ids, List<IInAppMessageLimit> limits);
}
