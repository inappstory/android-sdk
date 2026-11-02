package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.dialogstructure.ConfigV2;
import com.inappstory.sdk.stories.api.models.dialogstructure.SizeStructure;

import org.json.JSONObject;


public final class ToSizeStructure extends ToModelParser<SizeStructure> {
    public ToSizeStructure(JSONObject value) {
        super(value);
    }

    @Override
    public SizeStructure parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            SizeStructure structure = new SizeStructure();
            structure.width = (float) jsonObj.optDouble("width");
            structure.height = (float) jsonObj.optDouble("height");
            structure.left = (float) jsonObj.optDouble("left");
            structure.center = new ToCenterStructure(jsonObj.optJSONObject("center")).parse();
            return structure;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "SizeStructure";
    }
}
