package com.inappstory.sdk.network.utils.headers;

import com.inappstory.sdk.InAppStoryService;

public class XUserIdHeader implements Header {
    @Override
    public String getKey() {
        return HeadersKeys.USER_ID;
    }

    @Override
    public String getValue() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) {
            return service.getUserId();
        }
        return null;
    }
}
