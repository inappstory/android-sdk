package com.inappstory.sdk.newnetwork.utils.headers;

import com.inappstory.sdk.stories.api.models.Session;

public class AuthSessionIdHeader implements Header {
    @Override
    public String getKey() {
        return "auth-session-id";
    }

    @Override
    public String getValue() {
        if (Session.needToUpdate())
            return null;
        return Session.getInstance().id;
    }
}
