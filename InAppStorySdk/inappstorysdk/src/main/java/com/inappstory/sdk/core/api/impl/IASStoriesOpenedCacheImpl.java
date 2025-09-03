package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStoriesOpenedCache;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IASStoriesOpenedCacheImpl implements IASStoriesOpenedCache {
    private String localOpensKey;
    private final IASCore core;

    public IASStoriesOpenedCacheImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public String getLocalOpensKey(ContentType type) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        String uid = settingsHolder.userIdOrAnonymous();
        if (localOpensKey == null && uid != null) {
            localOpensKey = "opened" + uid;
        }
        return (type == ContentType.STORY) ? localOpensKey : type.name() + localOpensKey;
    }

    @Override
    public void clearLocalOpensKey() {
        localOpensKey = null;
    }

    @Override
    public void saveStoriesOpened(ContentType type) {
        String key = getLocalOpensKey(type);
        Set<String> opens = core.sharedPreferencesAPI().getStringSet(key);
        if (opens == null) opens = new HashSet<>();
        List<IListItemContent> stories = core.contentHolder().listsContent().getByType(type);
        for (IListItemContent story : stories) {
            if (story.isOpened()) {
                opens.add(Integer.toString(story.id()));
            } else if (opens.contains(Integer.toString(story.id()))) {
                story.setOpened(true);
            }
        }
        core.sharedPreferencesAPI().saveStringSet(key, opens);
    }

    @Override
    public void saveStoryOpened(int id, ContentType type) {
        String key = core.storyListCache().getLocalOpensKey(type);
        Set<String> opens = core.sharedPreferencesAPI().getStringSet(key);
        if (opens == null) opens = new HashSet<>();
        opens.add(Integer.toString(id));
        core.sharedPreferencesAPI().saveStringSet(key, opens);
    }
}
