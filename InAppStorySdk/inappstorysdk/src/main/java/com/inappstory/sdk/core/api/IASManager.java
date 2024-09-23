package com.inappstory.sdk.core.api;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public interface IASManager {
    InAppStoryManager create(
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
    );
}
