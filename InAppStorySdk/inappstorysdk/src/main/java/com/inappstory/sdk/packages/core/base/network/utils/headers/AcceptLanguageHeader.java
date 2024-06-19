package com.inappstory.sdk.packages.core.base.network.utils.headers;

import java.util.Locale;

public class AcceptLanguageHeader implements Header {
    public AcceptLanguageHeader(String languageTag) {
        if (languageTag == null) {
            this.languageTag = Locale.getDefault().toLanguageTag();
        } else {
            this.languageTag = languageTag;
        }
    }

    private final String languageTag;

    @Override
    public String getKey() {
        return HeadersKeys.ACCEPT_LANGUAGE;
    }

    @Override
    public String getValue() {
        return languageTag;
    }
}
