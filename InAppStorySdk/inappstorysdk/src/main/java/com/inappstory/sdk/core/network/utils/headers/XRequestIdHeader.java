package com.inappstory.sdk.core.network.utils.headers;


import java.util.UUID;

public class XRequestIdHeader implements Header {
    private String uuid = UUID.randomUUID().toString();

    @Override
    public String getKey() {
        return HeadersKeys.REQUEST_ID;
    }

    @Override
    public String getValue() {
        return uuid;
    }
}
