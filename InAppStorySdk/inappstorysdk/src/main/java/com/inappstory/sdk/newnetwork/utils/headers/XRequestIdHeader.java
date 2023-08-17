package com.inappstory.sdk.newnetwork.utils.headers;


import java.util.UUID;

public class XRequestIdHeader implements Header {
    private String uuid = UUID.randomUUID().toString();

    @Override
    public String getKey() {
        return "X-Request-ID";
    }

    @Override
    public String getValue() {
        return uuid;
    }
}
