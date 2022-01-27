package com.inappstory.sdk.stories.api.models.logs;

import java.util.ArrayList;
import java.util.List;

public class ApiLogRequest {
    public long timestamp;
    public String id;
    public List<ApiLogRequestHeader> headers = new ArrayList<>();
    public String method;
    public String url;
    public String body;
}
