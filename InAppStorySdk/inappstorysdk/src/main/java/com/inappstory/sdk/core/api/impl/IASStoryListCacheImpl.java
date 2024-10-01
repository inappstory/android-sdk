package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStoryListCache;
import com.inappstory.sdk.stories.api.models.Story;

public class IASStoryListCacheImpl implements IASStoryListCache {
    private String localOpensKey;
    private final IASCore core;

    public IASStoryListCacheImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void clearCachedLists() {

    }

    @Override
    public String getLocalOpensKey(Story.StoryType type) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (localOpensKey == null && settingsHolder.userId() != null) {
            localOpensKey = "opened" + settingsHolder.userId();
        }
        return (type == Story.StoryType.COMMON) ? localOpensKey : type.name() + localOpensKey;
    }
}
