package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Feed;
import com.inappstory.sdk.core.network.content.models.LayoutResponse;
import com.inappstory.sdk.parse.ExtendedJsonObject;

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
            layoutResponse.layout = jsonObj.getString("layout");
            layoutResponse.lastUpdatedAt = jsonObj.optStringNullable("last_updated_at");
            return layoutResponse;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "LayoutResponse";
    }
}
