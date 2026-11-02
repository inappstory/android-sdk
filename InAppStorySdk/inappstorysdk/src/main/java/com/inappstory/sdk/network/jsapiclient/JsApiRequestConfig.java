package com.inappstory.sdk.network.jsapiclient;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class JsApiRequestConfig {
    public String id;
    public String url;
    public String method;
    public String headers;
    public String params;
    public String data;
    public String cb;
    @SerializedName("profiling_key")
    public String profilingKey;
}
