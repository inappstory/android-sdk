package com.inappstory.sdk.network.utils.headers;

import android.os.Build;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;

import java.util.Locale;

public class AcceptLanguageHeader implements Header {
    @Override
    public String getKey() {
        return HeadersKeys.ACCEPT_LANGUAGE;
    }

    @Override
    public String getValue() {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager != null)
            return ((IASDataSettingsHolder) inAppStoryManager.iasCore().settingsAPI()).lang().toLanguageTag();
        return Locale.getDefault().toLanguageTag();
    }
}
