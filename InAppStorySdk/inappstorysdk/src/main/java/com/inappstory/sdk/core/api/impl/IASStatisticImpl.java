package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStatistic;
import com.inappstory.sdk.core.api.IASStatisticExceptions;
import com.inappstory.sdk.core.api.IASStatisticIAMV1;
import com.inappstory.sdk.core.api.IASStatisticProfiling;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.core.api.IASStatisticStoriesV2;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.IASStatisticIAMV1Impl;
import com.inappstory.sdk.stories.statistic.IASStatisticProfilingImpl;
import com.inappstory.sdk.stories.statistic.IASStatisticStoriesV1Impl;
import com.inappstory.sdk.stories.statistic.IASStatisticStoriesV2Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IASStatisticImpl implements IASStatistic {
    private final IASCore core;
    private final IASStatisticStoriesV2 iasStatisticStoriesV2;
    private final Map<String, IASStatisticStoriesV1> iasStatisticStoriesV1Map = new HashMap<>();
    private final IASStatisticProfiling iasStatisticProfiling;
    private final IASStatisticExceptions iasStatisticExceptions;
    private final IASStatisticIAMV1 iasStatisticIAMV1;


    public IASStatisticImpl(IASCore core) {
        this.core = core;
        iasStatisticStoriesV2 = new IASStatisticStoriesV2Impl(core);
        iasStatisticProfiling = new IASStatisticProfilingImpl(core);
        iasStatisticExceptions = new IASStatisticExceptionsImpl(core);
        iasStatisticIAMV1 = new IASStatisticIAMV1Impl(core);
    }

    @Override
    public IASStatisticStoriesV1 storiesV1() {
        String sessionId = ((IASDataSettingsHolder)core.settingsAPI()).sessionId();
        if (sessionId == null) return null;
        synchronized (v1Lock) {
            return iasStatisticStoriesV1Map.get(sessionId);
        }
    }

    @Override
    public void storiesV1(String sessionId, GetStatisticV1Callback callback) {
        if (sessionId == null) return;
        IASStatisticStoriesV1 statisticV1 = null;
        synchronized (v1Lock) {
            statisticV1 = iasStatisticStoriesV1Map.get(sessionId);
        }
        if (statisticV1 != null) {
            callback.get(statisticV1);
        }
    }

    @Override
    public void storiesV1(GetStatisticV1Callback callback) {
        IASStatisticStoriesV1 statisticV1 = storiesV1();
        if (statisticV1 != null) {
            callback.get(statisticV1);
        }
    }

    private final Object v1Lock = new Object();
    private final Object viewedLock = new Object();
    private final List<Integer> viewed = new ArrayList<>();

    @Override
    public void addViewedId(int id) {
        synchronized (viewedLock) {
            viewed.add(id);
        }
    }

    @Override
    public boolean hasViewedId(int id) {
        synchronized (viewedLock) {
            return viewed.contains(id);
        }
    }

    @Override
    public boolean hasViewedIds() {
        synchronized (viewedLock) {
            return viewed.size() > 0;
        }
    }

    @Override
    public void clearViewedIds() {
        synchronized (viewedLock) {
            viewed.clear();
        }
    }

    public List<Integer> newStatisticPreviews(List<Integer> vals) {
        List<Integer> sendObject = new ArrayList<>();
        synchronized (viewedLock) {
            for (Integer val : vals) {
                if (!viewed.contains(val)) {
                    sendObject.add(val);
                }
            }
        }
        return sendObject;
    }


    @Override
    public IASStatisticStoriesV2 storiesV2() {
        return iasStatisticStoriesV2;
    }

    @Override
    public IASStatisticIAMV1 iamV1() {
        return iasStatisticIAMV1;
    }

    @Override
    public IASStatisticProfiling profiling() {
        return iasStatisticProfiling;
    }

    @Override
    public IASStatisticExceptions exceptions() {
        return iasStatisticExceptions;
    }

    @Override
    public void changeSession(CachedSessionData sessionData, boolean disabled) {
        if (sessionData != null && sessionData.sessionId != null) {
            synchronized (v1Lock) {
                iasStatisticStoriesV1Map.put(
                        sessionData.sessionId,
                        new IASStatisticStoriesV1Impl(
                                core,
                                sessionData.userId,
                                sessionData.locale,
                                disabled
                        )
                );
            }
        }
        clearViewedIds();
    }

    @Override
    public void clearSession(String sessionId) {
        synchronized (v1Lock) {
            iasStatisticStoriesV1Map.remove(sessionId);
        }
        clearViewedIds();
    }
}
