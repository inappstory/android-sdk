package com.inappstory.sdk.network.jsapiclient;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class JsApiRequestConfig {
    @SerializedName(ModelFields.JsApiRequestConfig.id)
    public String id;
    @SerializedName(ModelFields.JsApiRequestConfig.url)
    public String url;
    @SerializedName(ModelFields.JsApiRequestConfig.method)
    public String method;
    @SerializedName(ModelFields.JsApiRequestConfig.headers)
    public String headers;
    @SerializedName(ModelFields.JsApiRequestConfig.params)
    public String params;
    @SerializedName(ModelFields.JsApiRequestConfig.data)
    public String data;
    @SerializedName(ModelFields.JsApiRequestConfig.cb)
    public String cb;
    @SerializedName(ModelFields.JsApiRequestConfig.profilingKey)
    public String profilingKey;
}
