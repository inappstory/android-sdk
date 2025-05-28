package com.inappstory.sdk.network.utils.headers;


public class AuthorizationHeader implements Header {
    private final String apiKey;

    public AuthorizationHeader(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String getKey() {
        return HeadersKeys.AUTHORIZATION;
    }

    @Override
    public String getValue() {
        return "Bearer " + apiKey;
    }
}
