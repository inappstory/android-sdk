package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStoriesOpenedCache;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;

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

    @Override
    public void saveStoriesOpened(List<Story> stories, Story.StoryType type) {
        String key = getLocalOpensKey(type);
        Set<String> opens = core.sharedPreferencesAPI().getStringSet(key);
        if (opens == null) opens = new HashSet<>();
        for (Story story : stories) {
            if (story.isOpened) {
                opens.add(Integer.toString(story.id));
            } else if (opens.contains(Integer.toString(story.id))) {
                story.isOpened = true;
            }
        }
        core.sharedPreferencesAPI().saveStringSet(key, opens);
    }

    @Override
    public void saveStoryOpened(int id, Story.StoryType type) {
        String key = core.storyListCache().getLocalOpensKey(type);
        Set<String> opens = core.sharedPreferencesAPI().getStringSet(key);
        if (opens == null) opens = new HashSet<>();
        opens.add(Integer.toString(id));
        core.sharedPreferencesAPI().saveStringSet(key, opens);
    }
}
