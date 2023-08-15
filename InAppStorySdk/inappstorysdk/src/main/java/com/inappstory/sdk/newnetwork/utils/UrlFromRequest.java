package com.inappstory.sdk.newnetwork.utils;

import android.util.Pair;

import com.inappstory.sdk.newnetwork.models.Request;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlFromRequest {
    public URL get(Request request) throws MalformedURLException {
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
}
