package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.dialogstructure.ButtonStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.InputStructure;

import org.json.JSONObject;


public final class ToButtonStructure extends ToModelParser<ButtonStructure> {
    public ToButtonStructure(JSONObject value) {
        super(value);
    }

    @Override
    public ButtonStructure parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            ButtonStructure structure = new ButtonStructure();
            structure.text = new ToTextStructure(jsonObj.optJSONObject("text")).parse();
            structure.border = new ToBorderStructure(jsonObj.optJSONObject("border")).parse();
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
        return "ButtonStructure";
    }
}
