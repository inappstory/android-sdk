package com.inappstory.sdk.network.utils;

import android.util.Pair;

import com.inappstory.sdk.network.models.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetUrl {
    public URL fromRequest(Request request) throws MalformedURLException {
        return fromPathAndParams(request.getUrl(), request.getVars(), request.getVarList());
    }

    private URL fromPathAndParams(
            String path,
            Map<String, String> queryParams,
            List<Pair<String, String>> dubParams
    ) throws MalformedURLException {
        StringBuilder varStr = new StringBuilder();
        Set<String> keys = new HashSet<>();
        if ((queryParams != null && !queryParams.isEmpty()) ||
                (dubParams != null && !dubParams.isEmpty())) {

            boolean hasParam = false;

            if (dubParams != null) {
                for (Pair<String, String> param : dubParams) {
                    if (param.second != null) {
                        keys.add(param.first);
                        varStr.append("&").append(param.first).append("=").append(param.second);
                        hasParam = true;
                    }
                }
            }
            if (queryParams != null)
                for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                    if (entry != null) {
                        if (keys.contains(entry.getKey())) continue;
                        keys.add(entry.getKey());
                        varStr.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                        hasParam = true;
                    }
                }
            varStr = new StringBuilder(hasParam ? "?" + varStr.substring(1) : "");
        }
        return new URL(path + varStr);
    }
}
