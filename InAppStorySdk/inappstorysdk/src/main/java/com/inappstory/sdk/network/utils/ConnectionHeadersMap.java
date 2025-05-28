package com.inappstory.sdk.network.utils;

import android.util.Pair;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionHeadersMap {
    public HashMap<String, String> get(HttpURLConnection connection) {
        HashMap<String, String> headers = new HashMap<>();
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            final String key = header.getKey();
            if (key != null) {
                headers.put(key, header.getValue().get(0));
            }
        }
        return headers;
    }
}
