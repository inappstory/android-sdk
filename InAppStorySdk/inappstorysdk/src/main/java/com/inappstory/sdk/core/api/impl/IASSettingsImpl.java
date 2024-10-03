package com.inappstory.sdk.core.api.impl;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettings;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;
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
    public void setUserId(final String newUserId) {
        if (newUserId == null || StringsUtils.getBytesLength(newUserId) > 255) {
           // showELog(IAS_ERROR_TAG, StringsUtils.getErrorStringFromContext(context, R.string.ias_setter_user_length_error));
            return;
        }
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService inAppStoryService) throws Exception {
                final String currentUserId;
                final Locale currentLang;
                synchronized (settingsLock) {
                    currentUserId = userId;
                    if (currentUserId != null && currentUserId.equals(newUserId)) return;
                    currentLang = lang;
                    userId = newUserId;
                }
                final String sessionId = core.sessionManager().getSession().getSessionId();

                core.storiesListVMHolder().clear();
                core.storyListCache().clearLocalOpensKey();
                core.screensManager().forceCloseAllReaders(new ForceCloseReaderCallback() {
                    @Override
                    public void onComplete() {
                        core.sessionManager().closeSession(
                                sendStatistic,
                                true,
                                currentLang,
                                currentUserId,
                                sessionId
                        );
                    }
                });

                if (inAppStoryService.getFavoriteImages() != null)
                    inAppStoryService.getFavoriteImages().clear();
                inAppStoryService.getStoryDownloadManager().refreshLocals(Story.StoryType.COMMON);
                inAppStoryService.getStoryDownloadManager().refreshLocals(Story.StoryType.UGC);
                inAppStoryService.getStoryDownloadManager().cleanTasks(false);
                inAppStoryService.setUserId(newUserId);
            }
        });

    }

    @Override
    public void setLang(final Locale newLang) {

        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService inAppStoryService) throws Exception {
                final Locale currentLang;
                final String currentUserId;
                synchronized (settingsLock) {
                    if (lang.toLanguageTag().equals(newLang.toLanguageTag())) return;
                    currentLang = lang;
                    lang = newLang;
                    currentUserId = userId;
                }
                final String sessionId = core.sessionManager().getSession().getSessionId();
                core.storiesListVMHolder().clear();
                core.storyListCache().clearLocalOpensKey();
                core.screensManager().forceCloseAllReaders(new ForceCloseReaderCallback() {
                    @Override
                    public void onComplete() {

                        core.sessionManager().closeSession(
                                sendStatistic,
                                true,
                                currentLang,
                                currentUserId,
                                sessionId
                        );
                    }
                });

                if (inAppStoryService.getFavoriteImages() != null)
                    inAppStoryService.getFavoriteImages().clear();
                inAppStoryService.getStoryDownloadManager().refreshLocals(Story.StoryType.COMMON);
                inAppStoryService.getStoryDownloadManager().refreshLocals(Story.StoryType.UGC);
                inAppStoryService.getStoryDownloadManager().cleanTasks(false);
            }
        });
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

    @Override
    public boolean gameDemoMode() {
        return gameDemoMode;
    }

    public void sendStatistic(boolean sendStatistic) {
        this.sendStatistic = sendStatistic;
    }

    private boolean sendStatistic = true;

    public void gameDemoMode(boolean gameDemoMode) {
        this.gameDemoMode = gameDemoMode;
    }

    private boolean gameDemoMode = false;

    @Override
    public boolean sendStatistic() {
        return sendStatistic;
    }
}
