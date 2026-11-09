package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.game.reader.GameSTIConfigData;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;


public final class ToGameSTIConfigData extends ToModelParser<GameSTIConfigData> {
    public ToGameSTIConfigData(JSONObject value) {
        super(value);
    }

    @Override
    public GameSTIConfigData parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameSTIConfigData data = new GameSTIConfigData();
            data.title = jsonObj.optStringNullable(
                    ModelFields.GameSTIConfigData.title
            );
            data.button = jsonObj.optStringNullable(
                    ModelFields.GameSTIConfigData.button
            );
            data.input = new ToGameSTIConfigInputData(jsonObj.optJSONObject(
                    ModelFields.GameSTIConfigData.input
            )).parse();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameSTIConfigData";
    }
}
