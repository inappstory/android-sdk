package com.inappstory.sdk.goods.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class ProductCartUpdateCallbacks {
    @SerializedName(ModelFields.ProductCartUpdateCallbacks.requestId)
    public String requestId;
    @SerializedName(ModelFields.ProductCartUpdateCallbacks.successCb)
    public String successCb;
    @SerializedName(ModelFields.ProductCartUpdateCallbacks.errorCb)
    public String errorCb;
}
