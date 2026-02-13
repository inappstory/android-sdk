package com.inappstory.sdk.refactoring.core.network.headers;

public class ContentTypeHeader implements Header {
    public ContentTypeHeader(boolean isFormEncoded, boolean hasBody) {
        this.isFormEncoded = isFormEncoded;
        this.hasBody = hasBody;
    }

    private boolean isFormEncoded;
    private boolean hasBody;

    @Override
    public String getKey() {
        return HeadersKeys.CONTENT_TYPE;
    }

    @Override
    public String getValue() {
        if (isFormEncoded) {
            return "application/x-www-form-urlencoded";
        } else if (hasBody) {
            return "application/json";
        } else
            return null;

    }
}
