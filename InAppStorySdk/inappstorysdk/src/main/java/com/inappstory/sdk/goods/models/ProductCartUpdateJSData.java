package com.inappstory.sdk.goods.models;

import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class ProductCartUpdateJSData {
    @SerializedName(ModelFields.ProductCartUpdateJSData.offer)
    public ProductCartOffer offer;
}
