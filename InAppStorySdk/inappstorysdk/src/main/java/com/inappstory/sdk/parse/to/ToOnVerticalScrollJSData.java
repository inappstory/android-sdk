package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.inappmessage.domain.reader.ShowSlideJSPayload;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
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
            data.scrollY = (float) jsonObj.optDouble(
                    ModelFields.OnVerticalScrollJSData.scrollY
            );
            data.scrollPercent = (float) jsonObj.optDouble(
                    ModelFields.OnVerticalScrollJSData.scrollPercent
            );
            data.isScrollableLayer = jsonObj.optBoolean(
                    ModelFields.OnVerticalScrollJSData.isScrollableLayer
            );
            data.cardId = jsonObj.optInt(
                    ModelFields.OnVerticalScrollJSData.cardId
            );
            data.slideIndex = jsonObj.optInt(
                    ModelFields.OnVerticalScrollJSData.slideIndex
            );
            data.layerIndex = jsonObj.optInt(
                    ModelFields.OnVerticalScrollJSData.layerIndex
            );

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
