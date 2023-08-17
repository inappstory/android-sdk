package com.inappstory.sdk.newnetwork.utils.headers;

import android.content.Context;

import com.inappstory.sdk.newnetwork.utils.UserAgent;

public class UserAgentHeader implements Header {
    private Context appContext;

    public UserAgentHeader(Context context) {
        if (context != null)
            this.appContext = context.getApplicationContext();
    }

    @Override
    public String getKey() {
        return "User-Agent";
    }

    @Override
    public String getValue() {
        return new UserAgent().generate(appContext);
    }
}
