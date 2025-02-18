package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.core.data.IInAppMessageLimit;
import com.inappstory.sdk.core.data.IInAppMessageLimitInCache;

import java.util.ArrayList;
import java.util.List;

public class InAppMessageLimitInCache implements IInAppMessageLimitInCache {
    private final long cacheTime;
    private final long expire;
    private final List<IInAppMessageLimit> limits;

    public InAppMessageLimitInCache(long expire, List<IInAppMessageLimit> limits) {
        this.cacheTime = System.currentTimeMillis();
        this.expire = expire;
        this.limits = new ArrayList<>(limits);
    }

    @Override
    public boolean isActive() {
        return (System.currentTimeMillis() - cacheTime) < (expire * 1000);
    }

    @Override
    public List<IInAppMessageLimit> limits() {
        return limits;
    }
}
