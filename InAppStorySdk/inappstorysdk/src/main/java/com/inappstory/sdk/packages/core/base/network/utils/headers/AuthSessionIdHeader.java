package com.inappstory.sdk.packages.core.base.network.utils.headers;

public class AuthSessionIdHeader implements Header {
    public AuthSessionIdHeader(String sessionId) {
        this.sessionId = sessionId;
    }

    private final String sessionId;

    @Override
    public String getKey() {
        return HeadersKeys.AUTH_SESSION_ID;
    }

    @Override
    public String getValue() {
        return sessionId;
    }
}
