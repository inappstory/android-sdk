package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.reader.GameAddToCachePayload;
import com.inappstory.sdk.game.reader.GameSTIData;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToGameSTIData extends ToModelParser<GameSTIData> {
    public ToGameSTIData(String value) {
        super(value);
    }



    @Override
    protected GameSTIData parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            GameSTIData stiData = new GameSTIData();
            stiData.cb = jsonObj.optStringNullable("cb");
            stiData.id = jsonObj.optStringNullable("id");
            stiData.config = new ToGameSTIConfigData(jsonObj.optJSONObject("config")).parse();

            return stiData;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameSTIData";
    }
}
