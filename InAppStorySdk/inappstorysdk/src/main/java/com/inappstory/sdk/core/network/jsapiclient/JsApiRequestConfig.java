package com.inappstory.sdk.core.network.jsapiclient;

import com.inappstory.sdk.core.network.annotations.models.SerializedName;

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
