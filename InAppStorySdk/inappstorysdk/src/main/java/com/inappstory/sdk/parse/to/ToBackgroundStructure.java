package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.BackgroundStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.CenterStructure;

import org.json.JSONObject;


public final class ToBackgroundStructure extends ToModelParser<BackgroundStructure> {
    public ToBackgroundStructure(JSONObject value) {
        super(value);
    }

    @Override
    public BackgroundStructure parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            BackgroundStructure structure = new BackgroundStructure();
            structure.color = jsonObj.optStringNullable(
                    ModelFields.BackgroundStructure.color
            );
            return structure;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "BackgroundStructure";
    }
}
