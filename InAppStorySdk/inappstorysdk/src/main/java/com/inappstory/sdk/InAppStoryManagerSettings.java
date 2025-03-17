package com.inappstory.sdk;

import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class InAppStoryManagerSettings {
    final String apiKey;
    final String testKey;
    final String userId;
    final String userSign;
    final Locale locale;
    final boolean gameDemoMode;
    final boolean isDeviceIDEnabled;
    final ArrayList<String> tags;
    final Map<String, String> placeholders;
    final Map<String, ImagePlaceholderValue> imagePlaceholders;

    public InAppStoryManagerSettings(
            String apiKey,
            String testKey,
            String userId,
            String userSign,
            Locale locale,
            boolean gameDemoMode,
            boolean isDeviceIDEnabled,
            ArrayList<String> tags,
            Map<String, String> placeholders,
            Map<String, ImagePlaceholderValue> imagePlaceholders
    ) {
        this.apiKey = apiKey;
        this.testKey = testKey;
        this.userId = userId;
        this.userSign = userSign;
        this.locale = locale;
        this.gameDemoMode = gameDemoMode;
        this.isDeviceIDEnabled = isDeviceIDEnabled;
        this.tags = tags;
        this.placeholders = placeholders;
        this.imagePlaceholders = imagePlaceholders;
    }
}
