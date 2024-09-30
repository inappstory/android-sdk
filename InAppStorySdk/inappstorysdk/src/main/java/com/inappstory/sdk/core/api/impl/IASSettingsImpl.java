package com.inappstory.sdk.core.api.impl;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASSettings;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IASSettingsImpl implements IASSettings {
    private final IASCore core;

    public IASSettingsImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void setUserId(String userId) {

    }

    @Override
    public void setLang(Locale lang) {

    }

    @Override
    public void setPlaceholders(@NonNull Map<String, String> newPlaceholders) {

    }

    @Override
    public void setImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> newPlaceholders) {

    }

    @Override
    public void setCommonAppearanceManager(AppearanceManager appearanceManager) {

    }

    @Override
    public void setTags(List<String> tags) {

    }
}
