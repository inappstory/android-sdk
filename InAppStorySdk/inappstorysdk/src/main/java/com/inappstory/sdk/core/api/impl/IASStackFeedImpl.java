package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASStackFeed;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;

import java.util.List;

public class IASStackFeedImpl implements IASStackFeed {
    private final IASCore core;

    public IASStackFeedImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void get(
            String feed,
            String uniqueStackId,
            AppearanceManager appearanceManager,
            List<String> tags,
            IStackFeedResult stackFeedResult
    ) {

    }
}
