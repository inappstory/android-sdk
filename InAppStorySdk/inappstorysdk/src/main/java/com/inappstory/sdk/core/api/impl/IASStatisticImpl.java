package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASStatistic;
import com.inappstory.sdk.core.api.IASStatisticExceptions;
import com.inappstory.sdk.core.api.IASStatisticProfiling;
import com.inappstory.sdk.core.api.IASStatisticV1;
import com.inappstory.sdk.core.api.IASStatisticV2;
import com.inappstory.sdk.stories.statistic.StatisticManager;

public class IASStatisticImpl implements IASStatistic {
    private final IASCore core;
    private final IASStatisticV2 iasStatisticV2;


    public IASStatisticImpl(IASCore core) {
        this.core = core;
        iasStatisticV2 = new StatisticManager(core);
    }

    @Override
    public IASStatisticV1 v1() {
        return null;
    }

    @Override
    public IASStatisticV2 v2() {
        return iasStatisticV2;
    }

    @Override
    public IASStatisticProfiling profiling() {
        return null;
    }

    @Override
    public IASStatisticExceptions exceptions() {
        return null;
    }
}
