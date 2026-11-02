package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.dialogstructure.BorderStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.PaddingStructure;

import org.json.JSONObject;


public final class ToPaddingStructure extends ToModelParser<PaddingStructure> {
    public ToPaddingStructure(JSONObject value) {
        super(value);
    }

    @Override
    public PaddingStructure parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            PaddingStructure structure = new PaddingStructure();
            structure.top = (float) jsonObj.optDouble("top");
            structure.bottom = (float) jsonObj.optDouble("bottom");
            structure.left = (float) jsonObj.optDouble("left");
            structure.right = (float) jsonObj.optDouble("right");
            return structure;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "PaddingStructure";
    }
}
