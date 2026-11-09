package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
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
            structure.align = jsonObj.optStringNullable(
                    ModelFields.TextStructure.align
            );
            structure.color = jsonObj.optStringNullable(
                    ModelFields.TextStructure.color
            );
            structure.value = jsonObj.optStringNullable(
                    ModelFields.TextStructure.value
            );
            structure.placeholder = jsonObj.optStringNullable(
                    ModelFields.TextStructure.placeholder
            );
            structure.family = jsonObj.optStringNullable(
                    ModelFields.TextStructure.family
            );
            structure.weight = jsonObj.optStringNullable(
                    ModelFields.TextStructure.weight
            );
            structure.style = jsonObj.optStringNullable(
                    ModelFields.TextStructure.style
            );
            structure.maxLines = jsonObj.optInt(
                    ModelFields.TextStructure.maxLines
            );
            structure.limit = jsonObj.optInt(
                    ModelFields.TextStructure.limit
            );
            structure.lineHeight = (float) jsonObj.optDouble(
                    ModelFields.TextStructure.lineHeight
            );
            structure.size = (float) jsonObj.optDouble(
                    ModelFields.TextStructure.size
            );
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
