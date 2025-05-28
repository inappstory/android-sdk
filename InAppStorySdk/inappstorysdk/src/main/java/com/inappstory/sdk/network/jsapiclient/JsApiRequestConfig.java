package com.inappstory.sdk.network.jsapiclient;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class JsApiRequestConfig {
    String id;
    String url;
    String method;
    String headers;
    String params;
    String data;
    String cb;
    @SerializedName("profiling_key")
    String profilingKey;
}
