package com.inappstory.sdk.core.api;

import android.util.Pair;

import com.inappstory.sdk.core.data.IAppVersion;
import com.inappstory.sdk.core.data.models.UniqueSessionParameters;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface IASDataSettingsHolder {
    IAppVersion externalAppVersion();
    String agentPrefix();
    String deviceId();
    String userId();
    String userIdOrAnonymous();
    CachedSessionData sessionData();
    String sessionId();
    String sessionIdOrEmpty();
    String userSign();
    boolean anonymous();
    Locale lang();
    boolean changeLayoutDirection();
    boolean isSoundOn();
    UniqueSessionParameters sessionParameters();
    Map<String, String> placeholders();
    Map<String, ImagePlaceholderValue> imagePlaceholders();
    Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> imagePlaceholdersWithSessionDefaults();
    List<String> tags();
    boolean noCorrectUserIdOrDevice();
    boolean noCorrectTags();
    boolean gameDemoMode();
    boolean sendStatistic();
}
