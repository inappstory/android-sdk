package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.reader.GameAddToCachePayload;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;


public final class ToGameAddToCachePayload extends ToModelParser<GameAddToCachePayload> {
    public ToGameAddToCachePayload(String value) {
        super(value);
    }



    @Override
    protected GameAddToCachePayload parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            GameAddToCachePayload payload = new GameAddToCachePayload();
            payload.id = jsonObj.optStringNullable(
                    ModelFields.GameAddToCachePayload.id
            );
            payload.cb = jsonObj.optStringNullable(
                    ModelFields.GameAddToCachePayload.cb
            );
            payload.webResource = new ToWebResource(jsonObj.optJSONObject(
                    ModelFields.GameAddToCachePayload.webResource
            )).parse();
            return payload;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameAddToCachePayload";
    }
}
