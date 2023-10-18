package com.inappstory.sdk.core.network.utils.headers;

import com.inappstory.sdk.stories.api.models.Session;

public class AuthSessionIdHeader implements Header {
    @Override
    public String getKey() {
        return HeadersKeys.AUTH_SESSION_ID;
    }

    @Override
    public String getValue() {
        if (Session.needToUpdate())
            return null;
        return Session.getInstance().id;
    }
}
