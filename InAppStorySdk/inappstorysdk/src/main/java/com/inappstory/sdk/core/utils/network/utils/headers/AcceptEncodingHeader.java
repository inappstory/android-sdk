package com.inappstory.sdk.core.utils.network.utils.headers;

public class AcceptEncodingHeader implements Header {
    @Override
    public String getKey() {
        return HeadersKeys.ACCEPT_ENCODING;
    }

    @Override
    public String getValue() {
        return "br, gzip";
    }
}
