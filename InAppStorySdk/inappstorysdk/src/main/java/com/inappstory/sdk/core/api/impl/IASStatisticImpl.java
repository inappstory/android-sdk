package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASStatistic;
import com.inappstory.sdk.core.api.IASStatisticExceptions;
import com.inappstory.sdk.core.api.IASStatisticProfiling;
import com.inappstory.sdk.core.api.IASStatisticV1;
import com.inappstory.sdk.core.api.IASStatisticV2;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.IASStatisticProfilingImpl;
import com.inappstory.sdk.stories.statistic.IASStatisticV1Impl;
import com.inappstory.sdk.stories.statistic.IASStatisticV2Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IASStatisticImpl implements IASStatistic {
    private final IASCore core;
    private final IASStatisticV2 iasStatisticV2;
    private final Map<String, IASStatisticV1> iasStatisticV1Map = new HashMap<>();
    private final IASStatisticProfiling iasStatisticProfiling;
    private final IASStatisticExceptions iasStatisticExceptions;


    public IASStatisticImpl(IASCore core) {
        this.core = core;
        iasStatisticV2 = new IASStatisticV2Impl(core);
        iasStatisticProfiling = new IASStatisticProfilingImpl(core);
        iasStatisticExceptions = new IASStatisticExceptionsImpl(core);
    }




    @Override
    public void createV1(String sessionId, boolean disabled) {
        synchronized (v1Lock) {
            iasStatisticV1Map.put(sessionId, new IASStatisticV1Impl(core, disabled));
        }
    }

    @Override
    public void removeV1(String sessionId) {
        synchronized (v1Lock) {
            iasStatisticV1Map.remove(sessionId);
        }
    }

    @Override
    public IASStatisticV1 v1() {
        String sessionId = core.sessionManager().getSession().getSessionId();
        if (sessionId == null) return null;
        synchronized (v1Lock) {
            return iasStatisticV1Map.get(sessionId);
        }
    }

    @Override
    public void v1(String sessionId, GetStatisticV1Callback callback) {
        if (sessionId == null) return;
        IASStatisticV1 statisticV1 = null;
        synchronized (v1Lock) {
            statisticV1 = iasStatisticV1Map.get(sessionId);
        }
        if (statisticV1 != null) {
            callback.get(statisticV1);
        }
    }

    @Override
    public void v1(GetStatisticV1Callback callback) {
        IASStatisticV1 statisticV1 = v1();
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
    public IASStatisticV2 v2() {
        return iasStatisticV2;
    }

    @Override
    public IASStatisticProfiling profiling() {
        return iasStatisticProfiling;
    }

    @Override
    public IASStatisticExceptions exceptions() {
        return iasStatisticExceptions;
    }
}
