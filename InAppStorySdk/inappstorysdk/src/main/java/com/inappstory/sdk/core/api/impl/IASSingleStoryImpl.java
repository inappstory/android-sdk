package com.inappstory.sdk.core.api.impl;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASSingleStory;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;

public class IASSingleStoryImpl implements IASSingleStory {
    private final IASCore core;

    public IASSingleStoryImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void showOnce(Context context, String storyId, AppearanceManager appearanceManager, IShowStoryOnceCallback callback) {

    }

    @Override
    public void show(Context context, String storyId, AppearanceManager appearanceManager, IShowStoryCallback callback, Integer slide) {

    }
}
