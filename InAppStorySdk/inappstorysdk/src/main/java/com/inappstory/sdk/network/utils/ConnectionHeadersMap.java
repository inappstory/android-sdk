package com.inappstory.sdk.network.utils;


import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionHeadersMap {
    public Map<String, String> get(HttpURLConnection connection) {
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            final String key = header.getKey();
            final List<String> value = header.getValue();
            if (key == null || key.isEmpty() || value == null || value.isEmpty())
                continue;
            headers.put(key, value.get(0));
        }
        return headers;
    }
}
