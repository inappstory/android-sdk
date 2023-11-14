package com.inappstory.sdk.core.utils.network.utils.headers;


import com.inappstory.sdk.core.utils.network.ApiSettings;

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
