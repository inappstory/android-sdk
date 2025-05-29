package com.inappstory.sdk.externalapi.settings;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASDataSettings;
import com.inappstory.sdk.core.data.IAppVersion;
import com.inappstory.sdk.core.data.IInAppStoryUserSettings;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IASSettingsExternalAPIImpl implements IASDataSettings {
    @Override
    public void destroy() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().destroy();
            }
        });
    }

    @Override
    public void inAppStorySettings(final IInAppStoryUserSettings settings) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().inAppStorySettings(settings);
            }
        });
    }

    @Override
    public void deviceId(final String deviceId) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().deviceId(deviceId);
            }
        });
    }

    public void setUserId(final String userId) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setUserId(userId, null);
            }
        });
    }

    public void setUserId(final String userId, final String sign) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setUserId(userId, sign);
            }
        });
    }

    @Override
    public void setExternalAppVersion(final IAppVersion externalAppVersion) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setExternalAppVersion(externalAppVersion);
            }
        });
    }

    @Override
    public void gameDemoMode(final boolean gameDemoMode) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().gameDemoMode(gameDemoMode);
            }
        });
    }

    public void setLang(final Locale lang, final boolean changeLayoutDirection) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setLang(lang, changeLayoutDirection);
            }
        });
    }

    @Override
    public void isSoundOn(boolean isSoundOn) {
        throw new NoSuchMethodError();
    }

    @Override
    public void sendStatistic(final boolean sendStatistic) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().sendStatistic(sendStatistic);
            }
        });
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
    public void addTags(final List<String> tags) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().addTags(tags);
            }
        });
    }

    @Override
    public void removeTags(final List<String> tags) {

        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().removeTags(tags);
            }
        });
    }

    @Override
    public void setPlaceholder(final String key, final String value) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setPlaceholder(key, value);
            }
        });
    }

    @Override
    public void setImagePlaceholder(final String key, final ImagePlaceholderValue value) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setImagePlaceholder(key, value);
            }
        });
    }
}
