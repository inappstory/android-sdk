package com.inappstory.sdk.goods.models;

import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;
import com.inappstory.sdk.network.annotations.models.SerializedName;

public class ProductCartUpdateJSData {
    @SerializedName("offer")
    public ProductCartOffer offer;
}
