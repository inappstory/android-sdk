package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.api.IASLimitsHolder;
import com.inappstory.sdk.core.data.IInAppMessageLimit;
import com.inappstory.sdk.core.network.content.models.InAppMessageLimitInCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IASLimitsHolderImpl implements IASLimitsHolder {
    @Override
    public List<IInAppMessageLimit> cachedLimitForIds(String ids) {
        if (ids == null) return null;
        synchronized (limitsLock) {
            InAppMessageLimitInCache limit = cachedLimits.get(ids);
            if (limit != null) {
                if (limit.isActive()) {
                    return limit.limits();
                } else {
                    cachedLimits.remove(ids);
                }
            }
        }
        return null;
    }

    private final Object limitsLock = new Object();
    private final Map<String, InAppMessageLimitInCache> cachedLimits = new HashMap<>();

    @Override
    public void addLimitToCache(String ids, List<IInAppMessageLimit> limits) {
        if (ids == null || ids.isEmpty() || limits == null || limits.isEmpty()) return;
        long minExpire = -1L;
        for (IInAppMessageLimit limit : limits) {
            if (minExpire == -1L || limit.expireInSeconds() < minExpire) {
                minExpire = limit.expireInSeconds();
            }
        }
        InAppMessageLimitInCache limitInCache = new InAppMessageLimitInCache(
                minExpire,
                limits
        );
        synchronized (limitsLock) {
            cachedLimits.put(ids, limitInCache);
        }
    }
}
