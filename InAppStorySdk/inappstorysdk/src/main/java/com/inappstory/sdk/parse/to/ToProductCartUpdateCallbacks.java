package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.goods.models.ProductCartUpdateCallbacks;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.WriteClipboardData;

import org.json.JSONObject;


public final class ToProductCartUpdateCallbacks extends ToModelParser<ProductCartUpdateCallbacks> {
    public ToProductCartUpdateCallbacks(String value) {
        super(value);
    }



    @Override
    protected ProductCartUpdateCallbacks parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            ProductCartUpdateCallbacks callbacks = new ProductCartUpdateCallbacks();
            callbacks.errorCb = jsonObj.optStringNullable("errorCb");
            callbacks.requestId = jsonObj.optStringNullable("requestId");
            callbacks.successCb = jsonObj.optStringNullable("successCb");

            return callbacks;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "ProductCartUpdateCallbacks";
    }
}
