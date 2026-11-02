package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.inappmessage.domain.reader.ShowSlideJSPayload;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.OnVerticalScrollJSData;

import org.json.JSONObject;


public final class ToOnVerticalScrollJSData extends ToModelParser<OnVerticalScrollJSData> {
    public ToOnVerticalScrollJSData(String value) {
        super(value);
    }



    @Override
    protected OnVerticalScrollJSData parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            OnVerticalScrollJSData data = new OnVerticalScrollJSData();
            data.scrollY = (float) jsonObj.optDouble("scrollY");
            data.scrollPercent = (float) jsonObj.optDouble("scrollPercent");
            data.isScrollableLayer = jsonObj.optBoolean("isScrollableLayer");
            data.cardId = jsonObj.optInt("cardId");
            data.slideIndex = jsonObj.optInt("slideIndex");
            data.layerIndex = jsonObj.optInt("layerIndex");

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "OnVerticalScrollJSData";
    }
}
