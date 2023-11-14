package com.inappstory.sdk.core.utils.network.utils.headers;

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
