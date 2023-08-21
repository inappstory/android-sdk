package com.inappstory.sdk.network.utils;

import android.util.Pair;

import com.inappstory.sdk.network.models.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

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
        if ((queryParams != null && !queryParams.isEmpty()) ||
                (dubParams != null && !dubParams.isEmpty())) {
            boolean hasParam = false;
            if (queryParams != null)
                for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                    if (entry != null) {
                        varStr.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                        hasParam = true;
                    }
                }
            if (dubParams != null) {
                for (Pair<String, String> param : dubParams) {
                    if (param.second != null) {
                        varStr.append("&").append(param.first).append("=").append(param.second);
                        hasParam = true;
                    }
                }
            }
            varStr = new StringBuilder(hasParam ? "?" + varStr.substring(1) : "");
        }
        return new URL(path + varStr);
    }
}
