package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.inappmessage.domain.reader.ShowSlideJSPayload;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;


public final class ToShowSlideJSPayload extends ToModelParser<ShowSlideJSPayload> {
    public ToShowSlideJSPayload(String value) {
        super(value);
    }



    @Override
    protected ShowSlideJSPayload parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            ShowSlideJSPayload payload = new ShowSlideJSPayload();
            payload.index = jsonObj.optInt(ModelFields.ShowSlideJSPayload.index);
            payload.cardId = jsonObj.optInt(ModelFields.ShowSlideJSPayload.cardId);

            return payload;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "ShowSlideJSPayload";
    }
}
