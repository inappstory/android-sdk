package com.inappstory.sdk.stories.api.models.logs;

import com.inappstory.sdk.network.constants.HttpMethods;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.utils.GetUrl;

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


    private String screening(String value) {
        if (value == null) return "''";
        return "'" + value.replace("'", "\\'") + "'";
    }

    public String getCurl() {
        ApiLogRequest request = this;
        StringBuilder current = new StringBuilder();
        String endLine = "\\\n";
        current.append("curl --location --request ").append(request.method).append(" ").append(request.url).append(" ").append(endLine);

        for (ApiLogRequestHeader entry : request.headers) {
            if (entry.key == null || entry.value == null) continue;
            current.append("--header ")
                    .append(screening(
                            entry.key + ": " + entry.value)
                    ).append(" ").append(endLine);
        }

        if (request.bodyUrlEncoded != null) {
            current.append("--data-urlencode ").append(screening(request.bodyUrlEncoded)).append(" ").append(endLine);
        }

        if (request.bodyRaw != null) {
            current.append("--data-raw ").append(screening(request.bodyRaw)).append(" ").append(endLine);
        } else if (request.body != null) {
            current.append("--data ").append(screening(request.body)).append(" ").append(endLine);
        }

        return current.toString();
    }

}
