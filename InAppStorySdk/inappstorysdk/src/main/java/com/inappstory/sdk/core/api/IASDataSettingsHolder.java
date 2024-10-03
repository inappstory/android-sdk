package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface IASDataSettingsHolder {
    void deviceId(String deviceId);
    String userId();
    Locale lang();
    Map<String, String> placeholders();
    Map<String, ImagePlaceholderValue> imagePlaceholders();
    List<String> tags();
    boolean noCorrectUserIdOrDevice();
    boolean noCorrectTags();
    boolean gameDemoMode();
    boolean sendStatistic();

}
