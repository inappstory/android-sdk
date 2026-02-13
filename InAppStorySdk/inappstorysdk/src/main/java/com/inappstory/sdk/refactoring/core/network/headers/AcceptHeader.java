package com.inappstory.sdk.refactoring.core.network.headers;

public class AcceptHeader implements Header {

    @Override
    public String getKey() {
        return HeadersKeys.ACCEPT;
    }

    @Override
    public String getValue() {
        return "application/json";
    }
}
