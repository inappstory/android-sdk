package com.inappstory.sdk.core.utils.network.jsapiclient;

import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

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
