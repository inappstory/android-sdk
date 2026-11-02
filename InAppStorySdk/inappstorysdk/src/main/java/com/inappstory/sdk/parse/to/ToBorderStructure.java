package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.dialogstructure.BackgroundStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.BorderStructure;

import org.json.JSONObject;


public final class ToBorderStructure extends ToModelParser<BorderStructure> {
    public ToBorderStructure(JSONObject value) {
        super(value);
    }

    @Override
    public BorderStructure parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            BorderStructure structure = new BorderStructure();
            structure.radius = (float) jsonObj.optDouble("radius");
            structure.width = (float) jsonObj.optDouble("width");
            structure.color = jsonObj.optStringNullable("color");
            return structure;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "BorderStructure";
    }
}
