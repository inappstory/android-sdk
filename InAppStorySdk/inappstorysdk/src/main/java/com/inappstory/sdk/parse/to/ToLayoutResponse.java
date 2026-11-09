package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Feed;
import com.inappstory.sdk.core.network.content.models.LayoutResponse;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;


public final class ToLayoutResponse extends ToModelParser<LayoutResponse> {
    public ToLayoutResponse(String value) {
        super(value);
    }

    @Override
    protected LayoutResponse parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            LayoutResponse layoutResponse = new LayoutResponse();
            layoutResponse.layout = jsonObj.getString(
                    ModelFields.LayoutResponse.layout
            );
            layoutResponse.lastUpdatedAt = jsonObj.optStringNullable(
                    ModelFields.LayoutResponse.lastUpdatedAt
            );
            return layoutResponse;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "LayoutResponse";
    }
}
