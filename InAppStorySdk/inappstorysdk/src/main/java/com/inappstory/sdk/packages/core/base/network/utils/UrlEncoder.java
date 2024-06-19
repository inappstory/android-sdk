package com.inappstory.sdk.packages.core.base.network.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlEncoder {
    public String encode(String toEncode) {
        try {
            return URLEncoder.encode(toEncode, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
        }
        return toEncode;
    }
}
