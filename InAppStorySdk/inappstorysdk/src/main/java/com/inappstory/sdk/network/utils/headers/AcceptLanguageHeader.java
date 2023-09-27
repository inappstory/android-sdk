package com.inappstory.sdk.network.utils.headers;

import android.os.Build;

import java.util.Locale;

public class AcceptLanguageHeader implements Header {
    @Override
    public String getKey() {
        return HeadersKeys.ACCEPT_LANGUAGE;
    }

    @Override
    public String getValue() {
        return Locale.getDefault().toLanguageTag();
    }
}
