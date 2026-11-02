package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.goods.models.ProductCartUpdateCallbacks;
import com.inappstory.sdk.goods.models.ProductCartUpdateJSData;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToProductCartUpdateJSData extends ToModelParser<ProductCartUpdateJSData> {
    public ToProductCartUpdateJSData(String value) {
        super(value);
    }

    @Override
    protected ProductCartUpdateJSData parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            ProductCartUpdateJSData data = new ProductCartUpdateJSData();
            data.offer = new ToProductCartOffer(jsonObj.optJSONObject("offer")).parse();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "ProductCartUpdateJSData";
    }
}
