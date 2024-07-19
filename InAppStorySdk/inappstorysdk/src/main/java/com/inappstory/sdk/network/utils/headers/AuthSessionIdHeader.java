package com.inappstory.sdk.network.utils.headers;


import androidx.annotation.NonNull;

public class AuthSessionIdHeader implements Header {
    public AuthSessionIdHeader(@NonNull String sessionId) {
        this.sessionId = sessionId;
    }

    private final @NonNull String sessionId;

    @Override
    public String getKey() {
        return HeadersKeys.AUTH_SESSION_ID;
    }

    @Override
    public String getValue() {
        return sessionId;
    }
}
