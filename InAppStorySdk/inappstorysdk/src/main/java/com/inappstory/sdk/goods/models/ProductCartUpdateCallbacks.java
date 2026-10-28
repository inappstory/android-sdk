package com.inappstory.sdk.goods.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class ProductCartUpdateCallbacks {
    @SerializedName("requestId")
    public String requestId;
    @SerializedName("successCb")
    public String successCb;
    @SerializedName("errorCb")
    public String errorCb;
}
