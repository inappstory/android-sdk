package com.inappstory.sdk.core.api.impl;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettings;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IASSettingsImpl implements IASDataSettings, IASDataSettingsHolder {
    private final IASCore core;
    private Locale lang;
    private final Map<String, String> placeholders = new HashMap<>();
    private final Map<String, ImagePlaceholderValue> imagePlaceholders = new HashMap<>();
    private final List<String> tags = new ArrayList<>();
    private String deviceId = null;
    private String userId;
    private final Object settingsLock = new Object();

    final static int TAG_LIMIT = 4000;

    public IASSettingsImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void setUserId(String userId) {
        synchronized (settingsLock) {
            this.userId = userId;
        }
    }

    @Override
    public void setLang(Locale lang) {
        synchronized (settingsLock) {
            this.lang = lang;
        }
    }

    @Override
    public void setPlaceholders(@NonNull Map<String, String> newPlaceholders) {
        synchronized (settingsLock) {
            this.placeholders.clear();
            this.placeholders.putAll(newPlaceholders);
        }
    }

    @Override
    public void setImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> newPlaceholders) {
        synchronized (settingsLock) {
            this.imagePlaceholders.clear();
            this.imagePlaceholders.putAll(newPlaceholders);
        }
    }

    @Override
    public void setCommonAppearanceManager(AppearanceManager appearanceManager) {
        AppearanceManager.setCommonInstance(appearanceManager);
    }

    @Override
    public void setTags(List<String> tags) {
        synchronized (settingsLock) {
            this.tags.clear();
            if (tags != null)
                this.tags.addAll(tags);
        }
    }

    @Override
    public void deviceId(String deviceId) {
        synchronized (settingsLock) {
            this.deviceId = deviceId;
        }
    }

    @Override
    public String userId() {
        synchronized (settingsLock) {
            return userId;
        }
    }

    @Override
    public Locale lang() {
        synchronized (settingsLock) {
            return lang;
        }
    }

    @Override
    public Map<String, String> placeholders() {
        synchronized (settingsLock) {
            return placeholders;
        }
    }

    @Override
    public Map<String, ImagePlaceholderValue> imagePlaceholders() {
        synchronized (settingsLock) {
            return imagePlaceholders;
        }
    }

    @Override
    public List<String> tags() {
        synchronized (settingsLock) {
            return tags;
        }
    }

    @Override
    public boolean noCorrectUserIdOrDevice() {
        synchronized (settingsLock) {
            return deviceId == null &&
                    (this.userId == null || StringsUtils.getBytesLength(this.userId) > 255);
        }
    }

    @Override
    public boolean noCorrectTags() {
        synchronized (settingsLock) {
            return StringsUtils.getBytesLength(TextUtils.join(",", tags)) > TAG_LIMIT;
        }
    }
}
