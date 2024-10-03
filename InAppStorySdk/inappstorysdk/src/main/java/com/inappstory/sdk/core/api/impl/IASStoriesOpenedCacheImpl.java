package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStoriesOpenedCache;
import com.inappstory.sdk.stories.api.models.Story;

public class IASStoriesOpenedCacheImpl implements IASStoriesOpenedCache {
    private String localOpensKey;
    private final IASCore core;

    public IASStoriesOpenedCacheImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public String getLocalOpensKey(Story.StoryType type) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (localOpensKey == null && settingsHolder.userId() != null) {
            localOpensKey = "opened" + settingsHolder.userId();
        }
        return (type == Story.StoryType.COMMON) ? localOpensKey : type.name() + localOpensKey;
    }

    @Override
    public void clearLocalOpensKey() {
        localOpensKey = null;
    }
}
