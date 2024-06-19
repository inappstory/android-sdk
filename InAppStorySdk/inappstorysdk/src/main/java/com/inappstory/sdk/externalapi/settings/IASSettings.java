package com.inappstory.sdk.externalapi.settings;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.OldInAppStoryManager;
import com.inappstory.sdk.UseOldManagerInstanceCallback;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class IASSettings {
    public void setUserId(final String userId) {
        OldInAppStoryManager.useInstance(new UseOldManagerInstanceCallback() {
            @Override
            public void use(@NonNull OldInAppStoryManager manager) throws Exception {
                manager.setUserId(userId);
            }
        });
    }

    public void setLang(final Locale lang) {
        OldInAppStoryManager.useInstance(new UseOldManagerInstanceCallback() {
            @Override
            public void use(@NonNull OldInAppStoryManager manager) throws Exception {
                manager.setLang(lang);
            }
        });
    }

    public void setPlaceholders(@NonNull final Map<String, String> newPlaceholders) {
        OldInAppStoryManager.useInstance(new UseOldManagerInstanceCallback() {
            @Override
            public void use(@NonNull OldInAppStoryManager manager) throws Exception {
                manager.setPlaceholders(newPlaceholders);
            }
        });
    }

    public void setImagePlaceholders(@NonNull final Map<String, ImagePlaceholderValue> newPlaceholders) {
        OldInAppStoryManager.useInstance(new UseOldManagerInstanceCallback() {
            @Override
            public void use(@NonNull OldInAppStoryManager manager) throws Exception {
                manager.setImagePlaceholders(newPlaceholders);
            }
        });
    }

    public void setCommonAppearanceManager(AppearanceManager appearanceManager) {
        AppearanceManager.setCommonInstance(appearanceManager);
    }

    public void setTags(final ArrayList<String> tags) {
        OldInAppStoryManager.useInstance(new UseOldManagerInstanceCallback() {
            @Override
            public void use(@NonNull OldInAppStoryManager manager) throws Exception {
                manager.setTags(tags);
            }
        });
    }
}
