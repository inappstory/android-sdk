package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.inappmessage.domain.reader.SlideLeftJSPayload;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToSlideLeftJSPayload extends ToModelParser<SlideLeftJSPayload> {
    public ToSlideLeftJSPayload(String value) {
        super(value);
    }



    @Override
    protected SlideLeftJSPayload parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            SlideLeftJSPayload payload = new SlideLeftJSPayload();
            payload.index = jsonObj.optInt("index");
            payload.cardId = jsonObj.optInt("cardId");

            return payload;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "SlideLeftJSPayload";
    }
}
