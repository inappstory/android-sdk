package com.inappstory.sdk.network.utils.headers;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.stories.api.models.CachedSessionData;

public class XUserIdHeader implements MutableHeader {
    @Override
    public String getKey() {
        return HeadersKeys.USER_ID;
    }

    String replacedValue = null;

    @Override
    public String getValue() {
        if (replacedValue != null) return replacedValue;
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return null;
        IASDataSettingsHolder settingsHolder =
                (IASDataSettingsHolder) inAppStoryManager.iasCore().settingsAPI();
        CachedSessionData sessionData = settingsHolder.sessionData();
        return sessionData != null ? sessionData.userId : null;
    }

    @Override
    public void setValue(String value) {
        replacedValue = value;
    }
}
