package com.inappstory.sdk.core.models.logs;

import com.inappstory.sdk.core.utils.network.constants.HttpMethods;
import com.inappstory.sdk.core.utils.network.models.Request;
import com.inappstory.sdk.core.utils.network.utils.GetUrl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        URL url = new GetUrl().fromRequest(request);
        this.method = request.getMethod();
        this.url = url.toString();
        this.id = requestId;
        if (!request.getMethod().equals(HttpMethods.GET)
                && !request.getMethod().equals(HttpMethods.HEAD)
                && request.getBody() != null
                && !request.getBody().isEmpty()
        ) {
            body = request.getBody();
            bodyRaw = request.getBodyRaw();
            bodyUrlEncoded = request.getBodyEncoded();
        }
    }

    public void setHeaders(Map<String,List<String>> entries) {
        for (Map.Entry<String, List<String>> entry : entries.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty())
                this.headers.add(new ApiLogRequestHeader(entry.getKey(), entry.getValue().get(0)));
        }
    }
}