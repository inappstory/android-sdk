package com.inappstory.sdk.packages.core.base.network.utils.headers;

public class XUserIdHeader implements MutableHeader {

    String replacedValue = null;
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
        if (replacedValue != null) return replacedValue;
        return userId;
    }

    @Override
    public void setValue(String value) {
        replacedValue = value;
    }
}
