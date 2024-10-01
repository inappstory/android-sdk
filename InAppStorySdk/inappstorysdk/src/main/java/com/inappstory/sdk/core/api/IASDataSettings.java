package com.inappstory.sdk.core.api;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface IASDataSettings {
    void setUserId(String userId);
    void setLang(Locale lang);
    void setPlaceholders(@NonNull Map<String, String> newPlaceholders);
    void setImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> newPlaceholders);
    void setCommonAppearanceManager(AppearanceManager appearanceManager);
    void setTags(List<String> tags);
}
