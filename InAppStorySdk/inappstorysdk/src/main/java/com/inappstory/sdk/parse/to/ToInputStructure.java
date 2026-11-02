package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.dialogstructure.InputStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.PaddingStructure;

import org.json.JSONObject;


public final class ToInputStructure extends ToModelParser<InputStructure> {
    public ToInputStructure(JSONObject value) {
        super(value);
    }

    @Override
    public InputStructure parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            InputStructure structure = new InputStructure();
            structure.text = new ToTextStructure(jsonObj.optJSONObject("text")).parse();
            structure.border = new ToBorderStructure(jsonObj.optJSONObject("border")).parse();
            structure.type = jsonObj.optStringNullable("type");
            structure.background = new ToBackgroundStructure(
                    jsonObj.optJSONObject("background")
            ).parse();
            structure.padding = new ToPaddingStructure(jsonObj.optJSONObject("padding")).parse();
            return structure;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "InputStructure";
    }
}
