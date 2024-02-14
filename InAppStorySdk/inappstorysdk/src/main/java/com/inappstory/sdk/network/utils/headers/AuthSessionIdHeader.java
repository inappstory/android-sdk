package com.inappstory.sdk.network.utils.headers;

import com.inappstory.sdk.InAppStoryService;

public class AuthSessionIdHeader implements Header {
    @Override
    public String getKey() {
        return HeadersKeys.AUTH_SESSION_ID;
    }

    @Override
    public String getValue() {
        InAppStoryService service = InAppStoryService.getInstance();
        return (service != null ? service.getSession().getSessionId() : null);
    }
}
