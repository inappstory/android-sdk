package com.inappstory.sdk.newnetwork.utils.headers;

import android.util.Pair;

import com.inappstory.sdk.network.ApiSettings;

public class AuthorizationHeader implements Header {
    @Override
    public String getKey() {
        return "Authorization";
    }

    @Override
    public String getValue() {
        return "Bearer " + ApiSettings.getInstance().getApiKey();
    }
}
