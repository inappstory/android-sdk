package com.inappstory.sdk.externalapi.iasmanager;

import com.inappstory.sdk.OldInAppStoryManager;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class IASManager {
    public OldInAppStoryManager create(
            String apiKey,
            String userId,
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
        OldInAppStoryManager.Builder builder = new OldInAppStoryManager.Builder();
        builder.lang(Locale.getDefault());
        builder.isDeviceIDEnabled(true);
        if (deviceIdEnabled != null) builder = builder.isDeviceIDEnabled(deviceIdEnabled);
        if (lang != null) builder = builder.lang(lang);
        if (sandbox != null) builder = builder.sandbox(sandbox);
        if (cacheSize != null) builder = builder.cacheSize(cacheSize);
        if (gameDemoMode != null) builder = builder.gameDemoMode(gameDemoMode);
        return builder
                .apiKey(apiKey)
                .userId(userId)
                .testKey(testKey)
                .tags(tags)
                .placeholders(placeholders)
                .imagePlaceholders(imagePlaceholders)
                .create();
    }
}
