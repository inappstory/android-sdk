package com.inappstory.sdk.packages.core.base.network.utils.headers;

public class UserAgentHeader implements Header {
    private final String userAgent;

    public UserAgentHeader(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String getKey() {
        return HeadersKeys.USER_AGENT;
    }

    @Override
    public String getValue() {
        return userAgent;
    }
}
