package com.inappstory.sdk.stories.api.models.logs;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.newnetwork.constants.HttpMethods;
import com.inappstory.sdk.newnetwork.models.Request;
import com.inappstory.sdk.newnetwork.utils.UrlFromRequest;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApiLogRequest {
    public long timestamp;
    public String id;
    public List<ApiLogRequestHeader> headers = new ArrayList<>();
    public String method;
    public String url;
    public String body;
    public String bodyRaw;
    public String bodyUrlEncoded;
    public boolean isStatic;

    public ApiLogRequest() {
        this.timestamp = System.currentTimeMillis();
        this.headers = new ArrayList<>();
    }

    public ApiLogRequest(long timestamp) {
        this.timestamp = timestamp;
        this.headers = new ArrayList<>();
    }
    public void buildFromRequest(Request request, String requestId) throws MalformedURLException {
        URL url = new UrlFromRequest().get(request);
        this.method = request.getMethod();
        this.url = url.toString();
        this.id = requestId;
        if (!request.getMethod().equals(HttpMethods.GET) && !request.getMethod().equals(HttpMethods.HEAD) && !request.getBody().isEmpty()) {
            body = request.getBody();
            bodyRaw = request.getBodyRaw();
            bodyUrlEncoded = request.getBodyEncoded();
            if (request.isFormEncoded()) {
                this.headers.add(
                        new ApiLogRequestHeader("Content-Type", "application/json"));
            }
        }
    }

    public void setHeaders(Map<String,List<String>> entries) {
        for (Map.Entry<String, List<String>> entry : entries.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty())
                this.headers.add(new ApiLogRequestHeader(entry.getKey(), entry.getValue().get(0)));
        }
    }
}
