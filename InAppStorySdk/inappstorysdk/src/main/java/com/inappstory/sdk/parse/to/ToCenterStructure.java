package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.CenterStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.SizeStructure;

import org.json.JSONObject;


public final class ToCenterStructure extends ToModelParser<CenterStructure> {
    public ToCenterStructure(JSONObject value) {
        super(value);
    }

    @Override
    public CenterStructure parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            CenterStructure structure = new CenterStructure();
            structure.x = (float) jsonObj.optDouble(
                    ModelFields.CenterStructure.x
            );
            structure.y = (float) jsonObj.optDouble(
                    ModelFields.CenterStructure.y
            );
            return structure;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "CenterStructure";
    }
}
