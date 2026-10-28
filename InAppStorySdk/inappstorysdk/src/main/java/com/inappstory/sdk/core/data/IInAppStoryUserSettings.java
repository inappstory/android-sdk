package com.inappstory.sdk.core.data;

import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface IInAppStoryUserSettings {
    String userId();
    String userSign();
    List<String> tags();
    Map<String, String> placeholders();
    Map<String, ImagePlaceholderValue> imagePlaceholders();
    Map<String, String> options();
    Locale lang();
    boolean anonymous();
    boolean changeLayoutDirection();
}
