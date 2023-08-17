package com.inappstory.sdk.newnetwork.utils.headers;

import com.inappstory.sdk.InAppStoryService;

public class XUserIdHeader implements Header {
    @Override
    public String getKey() {
        return "X-User-id";
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
