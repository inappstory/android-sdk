package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASManager;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class IASManagerImpl implements IASManager {
    private final IASCore core;

    public IASManagerImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public InAppStoryManager create(
            String apiKey,
            String userId,
            String userSign,
            Locale lang,
            ArrayList<String> tags,
            Map<String, String> placeholders,
            Map<String, ImagePlaceholderValue> imagePlaceholders,
            String testKey,
            Boolean gameDemoMode,
            Boolean deviceIdEnabled,
            Integer cacheSize,
            Boolean sandbox
    ) {
        return null;
    }
}
