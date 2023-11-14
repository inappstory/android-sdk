package com.inappstory.sdk.core.utils.network.utils.headers;

import android.content.Context;

import com.inappstory.sdk.core.utils.network.utils.UserAgent;

public class UserAgentHeader implements Header {
    private Context appContext;

    public UserAgentHeader(Context context) {
        if (context != null)
            this.appContext = context.getApplicationContext();
    }



    @Override
    public String getKey() {
        return HeadersKeys.USER_AGENT;
    }

    @Override
    public String getValue() {
        return new UserAgent().generate(appContext);
    }
}
