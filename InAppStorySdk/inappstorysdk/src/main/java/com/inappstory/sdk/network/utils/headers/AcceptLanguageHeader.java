package com.inappstory.sdk.network.utils.headers;

import com.inappstory.sdk.OldInAppStoryManager;

import java.util.Locale;

public class AcceptLanguageHeader implements Header {
    @Override
    public String getKey() {
        return HeadersKeys.ACCEPT_LANGUAGE;
    }

    @Override
    public String getValue() {
        OldInAppStoryManager inAppStoryManager = OldInAppStoryManager.getInstance();
        if (inAppStoryManager != null)
            return inAppStoryManager.getCurrentLocale().toLanguageTag();
        return Locale.getDefault().toLanguageTag();
    }
}
