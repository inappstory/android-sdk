package com.inappstory.sdk.refactoring.core.network.headers;


public class XUserIdHeader implements Header {
    private final String userId;

    public XUserIdHeader(String userId) {
        this.userId = userId;
    }

    @Override
    public String getKey() {
        return HeadersKeys.USER_ID;
    }

    @Override
    public String getValue() {
        return userId;
    }
}
