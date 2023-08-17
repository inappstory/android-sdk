package com.inappstory.sdk.newnetwork.utils.headers;

public class AcceptHeader implements Header {

    @Override
    public String getKey() {
        return "Accept";
    }

    @Override
    public String getValue() {
        return "application/json";
    }
}
