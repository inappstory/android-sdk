package com.inappstory.sdk.newnetwork.utils.headers;

public class ContentTypeHeader implements Header {
    public ContentTypeHeader(boolean isFormEncoded, boolean hasBody) {
        this.isFormEncoded = isFormEncoded;
        this.hasBody = hasBody;
    }

    private boolean isFormEncoded;
    private boolean hasBody;

    @Override
    public String getKey() {
        return "Content-Type";
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
