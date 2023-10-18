package com.inappstory.sdk.core.network.utils.headers;


import com.inappstory.sdk.core.network.ApiSettings;

public class AuthorizationHeader implements Header {
    @Override
    public String getKey() {
        return HeadersKeys.AUTHORIZATION;
    }

    @Override
    public String getValue() {
        return "Bearer " + ApiSettings.getInstance().getApiKey();
    }
}
