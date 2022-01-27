package com.inappstory.sdk.stories.api.models.logs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApiLogResponse {
    public long timestamp;
    public String id;
    public boolean isJson = false;
    public boolean isError = false;
    public String body;
    public List<ApiLogRequestHeader> responseHeaders = new ArrayList<>();
    public int status;
    public String errorBody;
    public long duration;

    public void generateError(int statusCode, String errorBody, HashMap<String, String> headers) {
        isError = true;
        this.status = statusCode;
        this.errorBody = errorBody;
        generateHeaders(headers);
    }

    public void generateJsonResponse(int statusCode, String body,
                                     HashMap<String, String> headers) {
        isJson = true;
        isError = false;
        this.status = statusCode;
        this.body = body;
        generateHeaders(headers);
    }

    private void generateHeaders(HashMap<String, String> headers) {
        if (headers != null) {
            for (String key : headers.keySet()) {
                responseHeaders.add(new ApiLogRequestHeader(key, headers.get(key)));
            }
        }
    }
}
