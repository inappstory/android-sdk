package com.inappstory.sdk.core.api;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.data.IAppVersion;
import com.inappstory.sdk.core.data.IInAppStoryUserSettings;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface IASDataSettings {
    void agentPrefix(String prefix);
    void destroy();
    void inAppStorySettings(IInAppStoryUserSettings settings);
    void deviceId(String deviceId);
    void setUserId(String userId, String sign);
    void setUserId(String userId);
    void setExternalAppVersion(IAppVersion externalAppVersion);
    void gameDemoMode(boolean gameDemoMode);
    void setLang(Locale lang, boolean changeLayoutDirection);
    void isSoundOn(boolean isSoundOn);
    void sendStatistic(boolean sendStatistic);
    void switchSoundOn();
    void setPlaceholders(@NonNull Map<String, String> newPlaceholders);
    void setImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> newPlaceholders);
    void setCommonAppearanceManager(AppearanceManager appearanceManager);
    void setTags(List<String> tags);
    void addTags(List<String> tags);
    void removeTags(List<String> tags);
    void setPlaceholder(String key, String value);
    void setImagePlaceholder(String key, ImagePlaceholderValue value);

    void refreshSession(
            final String currentUserId,
            final String currentDeviceId,
            final Locale currentLang,
            final boolean sendStatistic,
            final boolean anonymous
    );
}
