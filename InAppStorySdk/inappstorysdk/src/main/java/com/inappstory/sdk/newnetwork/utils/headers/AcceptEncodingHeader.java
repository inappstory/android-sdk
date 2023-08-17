package com.inappstory.sdk.newnetwork.utils.headers;

public class AcceptEncodingHeader implements Header {
    @Override
    public String getKey() {
        return "Accept-Encoding";
    }

    @Override
    public String getValue() {
        return "br, gzip";
    }
}
