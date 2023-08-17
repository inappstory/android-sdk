package com.inappstory.sdk.newnetwork.utils;

import static java.util.UUID.randomUUID;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.newnetwork.constants.HttpMethods;
import com.inappstory.sdk.newnetwork.models.Request;
import com.inappstory.sdk.stories.api.models.Session;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestConnection {
    public HttpURLConnection build(Request request, String requestId) throws IOException {
        URL url = new GetUrl().fromRequest(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod(request.getMethod());
        if (request.getHeaders() != null) {
            for (Object key : request.getHeaders().keySet()) {
                connection.setRequestProperty(key.toString(), request.getHeader(key));
            }
        }
        if (InAppStoryService.getInstance() != null && InAppStoryService.getInstance().getUserId() != null) {
            connection.setRequestProperty("X-User-id", InAppStoryService.getInstance().getUserId());

        }
        connection.setRequestProperty("Accept-Encoding", "br, gzip");
        connection.setRequestProperty("X-Request-ID", randomUUID().toString());
        if (request.isFormEncoded()) {
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }
        if (!Session.needToUpdate() && !request.getUrl().contains("session/open")) {
            connection.setRequestProperty("auth-session-id", Session.getInstance().id);
        }
        if (!request.getMethod().equals(HttpMethods.GET) &&
                !request.getMethod().equals(HttpMethods.HEAD) &&
                !request.getBody().isEmpty()) {
            new PostRequestBody().writeToStream(connection, request.getBody(), request.isFormEncoded());
            InAppStoryManager.showDLog("InAppStory_Network", requestId + " " + connection.getRequestProperties().toString());
            InAppStoryManager.showDLog("InAppStory_Network", requestId + " " + request.getBody());
        } else {
            InAppStoryManager.showDLog("InAppStory_Network", requestId + " " + connection.getRequestProperties().toString());
        }
        return connection;
    }
}
