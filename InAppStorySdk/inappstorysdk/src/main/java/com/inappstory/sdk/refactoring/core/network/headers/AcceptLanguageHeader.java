package com.inappstory.sdk.refactoring.core.network.headers;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;


public class AcceptLanguageHeader implements Header {
    private final IASCore core;

    public AcceptLanguageHeader(IASCore core) {
        this.core = core;
    }

    @Override
    public String getKey() {
        return HeadersKeys.ACCEPT_LANGUAGE;
    }

    @Override
    public String getValue() {
        return ((IASDataSettingsHolder)core.settingsAPI()).lang().toLanguageTag();
    }
}
