package com.inappstory.sdk.core.network.utils.headers;

public class CustomHeader implements Header {

    private String key;
    private String value;

    public CustomHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }
}
