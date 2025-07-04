package com.inappstory.sdk.network.utils.headers;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.utils.UserAgent;

public class UserAgentHeader implements Header {
    private IASCore core;

    public UserAgentHeader(IASCore core) {
        this.core = core;
    }

    @Override
    public String getKey() {
        return HeadersKeys.USER_AGENT;
    }

    @Override
    public String getValue() {
        return new UserAgent().generate(core);
    }
}
