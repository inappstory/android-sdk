package com.inappstory.sdk.network.utils.headers;


import com.inappstory.sdk.network.ApiSettings;

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
