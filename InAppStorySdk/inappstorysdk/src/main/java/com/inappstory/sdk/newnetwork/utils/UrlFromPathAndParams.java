package com.inappstory.sdk.newnetwork.utils;

import com.inappstory.sdk.network.NetworkClient;

import java.net.URL;
import java.util.Map;

public class UrlFromPathAndParams {
    public URL generate(String path, Map<String, String> queryParams) throws Exception {
        String url = NetworkClient.getInstance().getBaseUrl() + "v2/" + path;
        StringBuilder varStr = new StringBuilder();
        if (queryParams != null && queryParams.size() > 0) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                varStr.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
            varStr = new StringBuilder("?" + varStr.substring(1));
        }
        return new URL(url + varStr);
    }
}
