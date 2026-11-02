package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.dialogstructure.QuestionStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.TextStructure;

import org.json.JSONObject;


public final class ToTextStructure extends ToModelParser<TextStructure> {
    public ToTextStructure(JSONObject value) {
        super(value);
    }

    @Override
    public TextStructure parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            TextStructure structure = new TextStructure();
            structure.align = jsonObj.optStringNullable("align");
            structure.color = jsonObj.optStringNullable("color");
            structure.value = jsonObj.optStringNullable("value");
            structure.placeholder = jsonObj.optStringNullable("placeholder");
            structure.family = jsonObj.optStringNullable("family");
            structure.weight = jsonObj.optStringNullable("weight");
            structure.style = jsonObj.optStringNullable("style");
            structure.maxLines = jsonObj.optInt("maxLines");
            structure.limit = jsonObj.optInt("limit");
            structure.lineHeight = (float) jsonObj.optDouble("lineHeight");
            structure.size = (float) jsonObj.optDouble("size");
            return structure;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "TextStructure";
    }
}
