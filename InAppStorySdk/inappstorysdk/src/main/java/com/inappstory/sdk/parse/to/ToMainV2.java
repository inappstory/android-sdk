package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.ConfigV2;
import com.inappstory.sdk.stories.api.models.dialogstructure.MainV2;

import org.json.JSONObject;


public final class ToMainV2 extends ToModelParser<MainV2> {
    public ToMainV2(JSONObject value) {
        super(value);
    }

    @Override
    public MainV2 parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            MainV2 main = new MainV2();
            main.border = new ToBorderStructure(jsonObj.optJSONObject(
                    ModelFields.MainV2.border
            )).parse();
            main.background = new ToBackgroundStructure(jsonObj.optJSONObject(
                    ModelFields.MainV2.background
            )).parse();
            main.padding = new ToPaddingStructure(jsonObj.optJSONObject(
                    ModelFields.MainV2.padding
            )).parse();
            main.input = new ToInputStructure(jsonObj.optJSONObject(
                    ModelFields.MainV2.input
            )).parse();
            main.button = new ToButtonStructure(jsonObj.optJSONObject(
                    ModelFields.MainV2.button
            )).parse();
            main.question = new ToQuestionStructure(jsonObj.optJSONObject(
                    ModelFields.MainV2.question
            )).parse();
            return main;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "MainV2";
    }
}
