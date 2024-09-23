package com.inappstory.sdk.externalapi.settings;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.api.IASSettings;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IASSettingsExternalAPIImpl implements IASSettings {
    public void setUserId(final String userId) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.setUserId(userId);
            }
        });
    }

    public void setLang(final Locale lang) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.setLang(lang);
            }
        });
    }

    public void setPlaceholders(@NonNull final Map<String, String> newPlaceholders) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.setPlaceholders(newPlaceholders);
            }
        });
    }

    public void setImagePlaceholders(@NonNull final Map<String, ImagePlaceholderValue> newPlaceholders) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.setImagePlaceholders(newPlaceholders);
            }
        });
    }

    public void setCommonAppearanceManager(AppearanceManager appearanceManager) {
        AppearanceManager.setCommonInstance(appearanceManager);
    }

    public void setTags(final List<String> tags) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.setTags(new ArrayList<>(tags));
            }
        });
    }
}
