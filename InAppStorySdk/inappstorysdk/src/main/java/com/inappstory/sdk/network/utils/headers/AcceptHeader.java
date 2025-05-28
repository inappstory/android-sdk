package com.inappstory.sdk.network.utils.headers;

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
