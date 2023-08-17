package com.inappstory.sdk.newnetwork.utils;

import android.util.Pair;

import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.newnetwork.models.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class GetUrl {
    public URL fromRequest(Request request) throws MalformedURLException {
        String url = request.getUrl();
        StringBuilder varStr = new StringBuilder();
        if (!(request.getVarList().isEmpty() && request.getVars().isEmpty())) {
            for (Object key : request.getVarKeys()) {
                varStr.append("&").append(key).append("=").append(request.getVars().get(key));
            }
            for (Object keyVal : request.getVarList()) {
                Pair<String, String> locVal = (Pair<String, String>) keyVal;
                varStr.append("&").append(locVal.first).append("=").append(locVal.second);
            }
            varStr = new StringBuilder("?" + varStr.substring(1));
        }
        return new URL(url + varStr);
    }

    public URL fromPathAndParams(String path, Map<String, String> queryParams) throws MalformedURLException {
        String url = NetworkClient.getInstance().getBaseUrl() + "v2/" + path;
        String varStr = "";
        if (queryParams != null && queryParams.keySet().size() > 0) {
            for (Object key : queryParams.keySet()) {
                varStr += "&" + key + "=" + queryParams.get(key);
            }
            varStr = "?" + varStr.substring(1);
        }
        return new URL(url + varStr);
    }
}
