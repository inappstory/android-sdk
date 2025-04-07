package com.inappstory.sdk.core.api.impl;

import android.text.TextUtils;
import android.util.Pair;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettings;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.data.IAppVersion;
import com.inappstory.sdk.core.data.IInAppStoryUserSettings;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.core.network.content.models.StoryPlaceholder;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;
import com.inappstory.sdk.stories.utils.TagsUtils;
import com.inappstory.sdk.utils.format.StringsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class IASSettingsImpl implements IASDataSettings, IASDataSettingsHolder {
    private final IASCore core;
    private boolean isSoundOn = true;
    private Locale lang = Locale.getDefault();
    private final Map<String, String> userPlaceholders = new HashMap<>();
    private final Map<String, ImagePlaceholderValue> userImagePlaceholders = new HashMap<>();
    private final List<String> tags = new ArrayList<>();
    private String deviceId = null;
    private String userId;
    private String userSign;
    private final Object settingsLock = new Object();
    private IAppVersion externalAppVersion;

    public final static int TAG_LIMIT = 4000;

    public IASSettingsImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void setUserId(final String newUserId, final String newUserSign) {
        if (deviceId == null && (newUserId == null || newUserId.isEmpty())) {
            InAppStoryManager.showELog(
                    InAppStoryManager.IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            core.appContext(),
                            R.string.ias_usage_without_user_and_device
                    )
            );
            return;
        }
        if (newUserId == null || StringsUtils.getBytesLength(newUserId) > 255) {
            InAppStoryManager.showELog(
                    InAppStoryManager.IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            core.appContext(),
                            R.string.ias_setter_user_length_error
                    )
            );
            return;
        }
        final String currentUserId;
        final Locale currentLang;
        final String currentUserSign = userSign;
        synchronized (settingsLock) {
            currentUserId = userId;
            if (currentUserId != null && currentUserId.equals(newUserId)) {
                if (Objects.equals(userSign, newUserSign)) return;
            }
            userSign = newUserSign;
            currentLang = lang;
            userId = newUserId;
        }
        refreshSession(currentUserId, currentLang, currentUserSign);
    }

    private void refreshSession(final String currentUserId, final Locale currentLang, String currentUserSign) {
        final String sessionId = core.sessionManager().getSession().getSessionId();
        core.storiesListVMHolder().clear();
        core.storyListCache().clearLocalOpensKey();
        core.screensManager().forceCloseAllReaders(new ForceCloseReaderCallback() {
            @Override
            public void onComplete() {
                if (sessionId != null && !sessionId.isEmpty()) {
                    core.contentHolder().favoriteItems().clearByType(ContentType.STORY);
                    core.contentHolder().favoriteItems().clearByType(ContentType.UGC);
                    core.contentLoader().storyDownloadManager().refreshLocals(ContentType.STORY);
                    core.contentLoader().storyDownloadManager().refreshLocals(ContentType.UGC);
                    core.contentLoader().storyDownloadManager().cleanTasks(false);
                    core.contentHolder().readerContent().clearByType(ContentType.IN_APP_MESSAGE);
                    core.contentHolder().readerContent().clearByType(ContentType.STORY);
                    core.contentLoader().inAppMessageDownloadManager().clearLocalData();
                    core.contentLoader().inAppMessageDownloadManager().clearSlidesDownloader();
                    core.sessionManager().closeSession(
                            sendStatistic,
                            true,
                            currentLang.toLanguageTag(),
                            currentUserId,
                            sessionId
                    );
                } else {
                    core.inAppStoryService().getListReaderConnector().userIdChanged();
                }
            }
        });
    }

    @Override
    public void setExternalAppVersion(IAppVersion externalAppVersion) {
        this.externalAppVersion = externalAppVersion;
    }

    @Override
    public void setLang(final Locale newLang) {
        final Locale currentLang;
        final String currentUserId;
        final String currentUserSign;
        if (newLang == null) return;
        synchronized (settingsLock) {
            if (lang.toLanguageTag().equals(newLang.toLanguageTag())) return;
            currentLang = lang;
            lang = newLang;
            currentUserId = userId;
            currentUserSign = userSign;
        }
        refreshSession(currentUserId, currentLang, currentUserSign);
    }

    @Override
    public void isSoundOn(boolean isSoundOn) {
        synchronized (settingsLock) {
            this.isSoundOn = isSoundOn;
        }
    }

    @Override
    public void switchSoundOn() {
        synchronized (settingsLock) {
            this.isSoundOn = !this.isSoundOn;
        }
    }

    @Override
    public void setPlaceholders(@NonNull Map<String, String> newPlaceholders) {
        synchronized (settingsLock) {
            userPlaceholders.clear();
            userPlaceholders.putAll(newPlaceholders);
        }
    }

    @Override
    public void setImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> newPlaceholders) {
        synchronized (settingsLock) {
            this.userImagePlaceholders.clear();
            this.userImagePlaceholders.putAll(newPlaceholders);
        }
    }

    @Override
    public void setCommonAppearanceManager(AppearanceManager appearanceManager) {
        AppearanceManager.setCommonInstance(appearanceManager);
    }

    @Override
    public void setTags(List<String> tags) {

        Set<String> newList = new HashSet<>();
        if (tags != null) {
            for (String tag : tags) {
                if (!TagsUtils.checkTagPattern(tag)) {
                    InAppStoryManager.showELog(
                            InAppStoryManager.IAS_WARN_TAG,
                            StringsUtils.getFormattedErrorStringFromContext(
                                    core.appContext(),
                                    R.string.ias_tag_pattern_error,
                                    tag
                            )
                    );
                    continue;
                }
                newList.add(tag);
            }
        }
        if (StringsUtils.getBytesLength(TextUtils.join(",", newList)) > TAG_LIMIT) {
            InAppStoryManager.showELog(
                    InAppStoryManager.IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            core.appContext(),
                            R.string.ias_setter_tags_length_error
                    )
            );
            return;
        }
        List<String> currentTags = tags();
        Set<String> oldList = new HashSet<>();
        if (currentTags != null) {
            oldList.addAll(currentTags);
        }
        boolean replace = false;
        if (oldList.size() == newList.size()) {
            for (String newTag : newList) {
                if (!oldList.contains(newTag)) {
                    replace = true;
                    break;
                }
            }
        } else {
            replace = true;
        }

        if (replace) {
            synchronized (settingsLock) {
                this.tags.clear();
                this.tags.addAll(newList);
                core.storiesListVMHolder().clear();
            }
        }
    }


    @Override
    public void addTags(List<String> newTags) {
        boolean hasNewTags = false;
        if (newTags == null || newTags.isEmpty()) return;
        final Set<String> mergedTags = new HashSet<>();
        synchronized (settingsLock) {
            mergedTags.addAll(tags);
        }
        for (String tag : newTags) {
            if (!TagsUtils.checkTagPattern(tag)) {
                StringsUtils.getFormattedErrorStringFromContext(
                        core.appContext(),
                        R.string.ias_tag_pattern_error,
                        tag
                );
                continue;
            }
            if (!mergedTags.contains(tag)) {
                mergedTags.add(tag);
                hasNewTags = true;
            }
        }
        String tagsString = TextUtils.join(",", mergedTags);
        if (StringsUtils.getBytesLength(tagsString) > TAG_LIMIT) {
            StringsUtils.getErrorStringFromContext(
                    core.appContext(),
                    R.string.ias_setter_tags_length_error
            );
            return;
        }
        if (hasNewTags) {
            synchronized (settingsLock) {
                tags.clear();
                tags.addAll(mergedTags);
            }
            core.storiesListVMHolder().clear();
        }
    }

    @Override
    public void removeTags(List<String> removedTags) {
        boolean tagIsRemoved = false;
        if (removedTags == null || removedTags.isEmpty()) return;
        List<String> currentTags = new ArrayList<>();
        synchronized (settingsLock) {
            currentTags.addAll(tags);
        }

        for (String tag : removedTags) {
            if (currentTags.contains(tag)) {
                tagIsRemoved = true;
                currentTags.remove(tag);
            }
        }
        if (tagIsRemoved) {
            synchronized (settingsLock) {
                tags.clear();
                tags.addAll(currentTags);
            }
            core.storiesListVMHolder().clear();
        }
    }

    private final Map<String, String> sessionPlaceholders = new HashMap<>();
    private final Map<String, ImagePlaceholderValue> sessionImagePlaceholders = new HashMap<>();

    public void sessionPlaceholders(List<StoryPlaceholder> sessionPlaceholders) {
        if (sessionPlaceholders == null) return;
        synchronized (settingsLock) {
            this.sessionPlaceholders.clear();
            for (StoryPlaceholder storyPlaceholder : sessionPlaceholders) {
                if (storyPlaceholder.name != null && storyPlaceholder.defaultVal != null)
                    this.sessionPlaceholders.put(storyPlaceholder.name, storyPlaceholder.defaultVal);
            }
        }
    }

    public void sessionImagePlaceholders(List<StoryPlaceholder> sessionPlaceholders) {
        if (sessionPlaceholders == null) return;
        synchronized (settingsLock) {
            this.sessionImagePlaceholders.clear();
            for (StoryPlaceholder storyPlaceholder : sessionPlaceholders) {
                if (storyPlaceholder.name != null && storyPlaceholder.defaultVal != null)
                    if (!URLUtil.isNetworkUrl(storyPlaceholder.defaultVal))
                        continue;
                ImagePlaceholderValue defaultVal = ImagePlaceholderValue.createByUrl(storyPlaceholder.defaultVal);
                this.sessionImagePlaceholders.put(
                        storyPlaceholder.name, defaultVal
                );
            }
        }
    }

    @Override
    public void setPlaceholder(String key, String value) {
        synchronized (settingsLock) {
            userPlaceholders.put(key, value);
        }
    }

    @Override
    public void setImagePlaceholder(String key, ImagePlaceholderValue value) {
        synchronized (settingsLock) {
            userImagePlaceholders.put(key, value);
        }
    }

    @Override
    public void destroy() {
        final Locale currentLang;
        final String currentUserId;
        synchronized (settingsLock) {
            currentLang = lang;
            currentUserId = userId;
            this.userId = null;
            this.userSign = null;
            this.tags.clear();
            this.userImagePlaceholders.clear();
            this.userPlaceholders.clear();
            this.lang = Locale.getDefault();
        }
        final String sessionId = core.sessionManager().getSession().getSessionId();
        if (sessionId != null) {
            core.sessionManager().closeSession(
                    sendStatistic,
                    true,
                    currentLang.toLanguageTag(),
                    currentUserId,
                    sessionId
            );
        }
    }

    @Override
    public void inAppStorySettings(IInAppStoryUserSettings settings) {
        final Locale currentLang;
        final String currentUserId;
        final String currentUserSign;
        boolean needToReloadSession = false;
        synchronized (settingsLock) {
            currentLang = lang;
            currentUserId = userId;
            currentUserSign = userSign;
            if (settings.userId() != null) {
                if (deviceId == null && settings.userId().isEmpty()) {
                    InAppStoryManager.showELog(
                            InAppStoryManager.IAS_ERROR_TAG,
                            StringsUtils.getErrorStringFromContext(
                                    core.appContext(),
                                    R.string.ias_usage_without_user_and_device
                            )
                    );
                    return;
                }
                if (StringsUtils.getBytesLength(settings.userId()) > 255) {
                    InAppStoryManager.showELog(
                            InAppStoryManager.IAS_ERROR_TAG,
                            StringsUtils.getErrorStringFromContext(
                                    core.appContext(),
                                    R.string.ias_setter_user_length_error
                            )
                    );
                    return;
                }
                if (currentUserId != null && currentUserId.equals(settings.userId())) {
                    if (!Objects.equals(userSign, settings.userSign())) {
                        userSign = settings.userSign();
                        userId = settings.userId();
                        needToReloadSession = true;
                    }
                } else if (currentUserId == null) {
                    userSign = settings.userSign();
                    userId = settings.userId();
                }
            }
            if (settings.tags() != null) {
                List<String> filteredList = new ArrayList<>();
                for (String tag : settings.tags()) {
                    if (!TagsUtils.checkTagPattern(tag)) {
                        InAppStoryManager.showELog(
                                InAppStoryManager.IAS_WARN_TAG,
                                StringsUtils.getFormattedErrorStringFromContext(
                                        core.appContext(),
                                        R.string.ias_tag_pattern_error,
                                        tag
                                )
                        );
                        continue;
                    }
                    filteredList.add(tag);
                }
                if (StringsUtils.getBytesLength(TextUtils.join(",", filteredList)) > TAG_LIMIT) {
                    InAppStoryManager.showELog(
                            InAppStoryManager.IAS_ERROR_TAG,
                            StringsUtils.getErrorStringFromContext(
                                    core.appContext(),
                                    R.string.ias_setter_tags_length_error
                            )
                    );
                    return;

                }
                List<String> currentTags = tags();
                Set<String> oldList = new HashSet<>();
                if (currentTags != null) {
                    oldList.addAll(currentTags);
                }
                Set<String> newList = new HashSet<>(filteredList);
                boolean replace = false;
                if (oldList.size() == newList.size()) {
                    for (String newTag : newList) {
                        if (!oldList.contains(newTag)) {
                            replace = true;
                            break;
                        }
                    }
                } else {
                    replace = true;
                }

                if (replace) {
                    this.tags.clear();
                    this.tags.addAll(newList);
                    core.storiesListVMHolder().clear();
                }
            }
            if (settings.imagePlaceholders() != null) {
                this.userImagePlaceholders.clear();
                this.userImagePlaceholders.putAll(settings.imagePlaceholders());
            }
            if (settings.placeholders() != null) {
                this.userPlaceholders.clear();
                this.userPlaceholders.putAll(settings.placeholders());
            }
            if (settings.lang() != null) {
                if (!lang.toLanguageTag().equals(settings.lang().toLanguageTag())) {
                    lang = settings.lang();
                    needToReloadSession = true;
                }
            }
        }
        if (needToReloadSession) {
            refreshSession(currentUserId, currentLang, currentUserSign);
        }
    }

    @Override
    public void deviceId(String deviceId) {
        synchronized (settingsLock) {
            this.deviceId = deviceId;
        }
    }

    @Override
    public void setUserId(String userId) {
        setUserId(userId, null);
    }

    @Override
    public IAppVersion externalAppVersion() {
        return externalAppVersion;
    }

    @Override
    public String deviceId() {
        synchronized (settingsLock) {
            return deviceId;
        }
    }

    @Override
    public String userId() {
        synchronized (settingsLock) {
            return userId;
        }
    }

    @Override
    public String userSign() {
        synchronized (settingsLock) {
            return userSign;
        }
    }

    @Override
    public Locale lang() {
        synchronized (settingsLock) {
            return lang;
        }
    }

    @Override
    public boolean isSoundOn() {
        synchronized (settingsLock) {
            return isSoundOn;
        }
    }

    @Override
    public Map<String, String> placeholders() {
        Map<String, String> merged = new HashMap<>();
        synchronized (settingsLock) {
            for (Map.Entry<String, String> placeholder : sessionPlaceholders.entrySet()) {
                if (placeholder.getKey() == null || placeholder.getValue() == null) continue;
                merged.put(placeholder.getKey(), placeholder.getValue());
            }
            for (Map.Entry<String, String> placeholder : userPlaceholders.entrySet()) {
                if (placeholder.getKey() == null || placeholder.getValue() == null) continue;
                merged.put(placeholder.getKey(), placeholder.getValue());
            }
        }
        return merged;
    }

    @Override
    public Map<String, ImagePlaceholderValue> imagePlaceholders() {
        Map<String, ImagePlaceholderValue> merged = new HashMap<>();
        synchronized (settingsLock) {
            for (Map.Entry<String, ImagePlaceholderValue> placeholder : sessionImagePlaceholders.entrySet()) {
                if (placeholder.getKey() == null || placeholder.getValue() == null) continue;
                merged.put(placeholder.getKey(), placeholder.getValue());
            }
            for (Map.Entry<String, ImagePlaceholderValue> placeholder : userImagePlaceholders.entrySet()) {
                if (placeholder.getKey() == null || placeholder.getValue() == null) continue;
                merged.put(placeholder.getKey(), placeholder.getValue());
            }
        }
        return merged;
    }

    @Override
    public Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> imagePlaceholdersWithSessionDefaults() {
        Set<String> mergedKeySet = new HashSet<>();
        Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> result = new HashMap<>();
        synchronized (settingsLock) {
            mergedKeySet.addAll(userImagePlaceholders.keySet());
            mergedKeySet.addAll(sessionImagePlaceholders.keySet());
            for (String key : mergedKeySet) {
                ImagePlaceholderValue userKey = userImagePlaceholders.get(key);
                ImagePlaceholderValue sessionKey = sessionImagePlaceholders.get(key);
                if (userKey != null) {
                    result.put(
                            key,
                            new Pair<>(
                                    userKey,
                                    sessionKey
                            )
                    );
                } else {
                    result.put(
                            key,
                            new Pair<ImagePlaceholderValue, ImagePlaceholderValue>(
                                    sessionKey,
                                    null
                            )
                    );
                }
            }
        }
        return result;
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
            if (StringsUtils.getBytesLength(userId) > 255) {
                return true;
            }
            if (deviceId == null && (userId == null || userId.isEmpty())) {
                return true;
            }
            return false;
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
