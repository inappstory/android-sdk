package com.inappstory.sdk.externalapi.settings;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASDataSettings;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IASSettingsExternalAPIImpl implements IASDataSettings {
    @Override
    public void deviceId(String deviceId) {
        throw new NoSuchMethodError();
    }

    public void setUserId(final String userId) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setUserId(userId);
            }
        });
    }

    @Override
    public void gameDemoMode(boolean gameDemoMode) {
        throw new NoSuchMethodError();
    }

    public void setLang(final Locale lang) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setLang(lang);
            }
        });
    }

    @Override
    public void isSoundOn(boolean isSoundOn) {
        throw new NoSuchMethodError();
    }

    @Override
    public void switchSoundOn() {
        throw new NoSuchMethodError();
    }

    public void setPlaceholders(@NonNull final Map<String, String> newPlaceholders) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setPlaceholders(newPlaceholders);
            }
        });
    }

    public void setImagePlaceholders(@NonNull final Map<String, ImagePlaceholderValue> newPlaceholders) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setImagePlaceholders(newPlaceholders);
            }
        });
    }

    public void setCommonAppearanceManager(AppearanceManager appearanceManager) {
        AppearanceManager.setCommonInstance(appearanceManager);
    }

    public void setTags(final List<String> tags) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setTags(tags);
            }
        });
    }

    @Override
    public void addTags(List<String> tags) {
        throw new NoSuchMethodError();
    }

    @Override
    public void removeTags(List<String> tags) {
        throw new NoSuchMethodError();
    }

    @Override
    public void setPlaceholder(String key, String value) {
        throw new NoSuchMethodError();
    }

    @Override
    public void setImagePlaceholder(String key, ImagePlaceholderValue value) {
        throw new NoSuchMethodError();
    }
}
