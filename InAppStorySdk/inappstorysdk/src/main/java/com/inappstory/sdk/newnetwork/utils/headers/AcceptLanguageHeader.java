package com.inappstory.sdk.newnetwork.utils.headers;

import android.os.Build;

import java.util.Locale;

public class AcceptLanguageHeader implements Header {
    @Override
    public String getKey() {
        return "Accept-Language";
    }

    @Override
    public String getValue() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Locale.getDefault().toLanguageTag();
        } else {
            return Locale.getDefault().getLanguage();
        }
    }
}
