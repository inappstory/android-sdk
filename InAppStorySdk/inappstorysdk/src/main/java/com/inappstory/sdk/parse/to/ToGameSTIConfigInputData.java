package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.game.reader.GameSTIConfigData;
import com.inappstory.sdk.game.reader.GameSTIConfigInputData;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;


public final class ToGameSTIConfigInputData extends ToModelParser<GameSTIConfigInputData> {
    public ToGameSTIConfigInputData(JSONObject value) {
        super(value);
    }

    @Override
    public GameSTIConfigInputData parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameSTIConfigInputData data = new GameSTIConfigInputData();
            data.type = jsonObj.optStringNullable(
                    ModelFields.GameSTIConfigInputData.type
            );
            data.placeholder = jsonObj.optStringNullable(
                    ModelFields.GameSTIConfigInputData.placeholder
            );
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameSTIConfigInputData";
    }
}
