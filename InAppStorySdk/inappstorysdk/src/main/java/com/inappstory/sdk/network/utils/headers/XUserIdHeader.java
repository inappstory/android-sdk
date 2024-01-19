package com.inappstory.sdk.network.utils.headers;

import com.inappstory.sdk.InAppStoryService;

public class XUserIdHeader implements MutableHeader {
    @Override
    public String getKey() {
        return HeadersKeys.USER_ID;
    }

    String replacedValue = null;

    @Override
    public String getValue() {
        if (replacedValue != null) return replacedValue;
        InAppStoryService service = InAppStoryService.getInstance();
        return service != null ? service.getUserId() : null;
    }

    @Override
    public void setValue(String value) {
        replacedValue = value;
    }
}
