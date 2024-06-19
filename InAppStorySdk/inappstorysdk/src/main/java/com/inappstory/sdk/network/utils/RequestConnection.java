package com.inappstory.sdk.network.utils;


import android.util.Pair;

import com.inappstory.sdk.OldInAppStoryManager;
import com.inappstory.sdk.network.constants.HttpMethods;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.utils.headers.Header;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class RequestConnection {
    public Pair<HttpURLConnection, Map<String, List<String>>> build(Request request, String requestId) throws IOException {
        URL url = new GetUrl().fromRequest(request);
        Map<String, List<String>> connectionProperties = null;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod(request.getMethod());
        if (request.getHeaders() != null) {
            for (Object header : request.getHeaders()) {
                connection.setRequestProperty(((Header)header).getKey(), ((Header)header).getValue());
            }
        }
        connectionProperties = connection.getRequestProperties();
        if (!request.getMethod().equals(HttpMethods.GET) &&
                !request.getMethod().equals(HttpMethods.HEAD) &&
                request.getBody() != null &&
                !request.getBody().isEmpty()
        ) {
            OldInAppStoryManager.showDLog("InAppStory_Network", requestId + " " + connectionProperties);
            new PostRequestBody().writeToStream(connection, request.getBody());
            OldInAppStoryManager.showDLog("InAppStory_Network", requestId + " " + request.getBody());
        } else {
            OldInAppStoryManager.showDLog("InAppStory_Network", requestId + " " + connectionProperties);
        }
        return new Pair<>(connection, connectionProperties);
    }
}
