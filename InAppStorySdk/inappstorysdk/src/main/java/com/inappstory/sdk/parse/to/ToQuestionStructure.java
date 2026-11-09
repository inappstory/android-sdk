package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.ButtonStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.QuestionStructure;

import org.json.JSONObject;


public final class ToQuestionStructure extends ToModelParser<QuestionStructure> {
    public ToQuestionStructure(JSONObject value) {
        super(value);
    }

    @Override
    public QuestionStructure parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            QuestionStructure structure = new QuestionStructure();
            structure.text = new ToTextStructure(jsonObj.optJSONObject(
                    ModelFields.QuestionStructure.text
            )).parse();
            structure.padding = new ToPaddingStructure(jsonObj.optJSONObject(
                    ModelFields.QuestionStructure.padding
            )).parse();
            return structure;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "QuestionStructure";
    }
}
