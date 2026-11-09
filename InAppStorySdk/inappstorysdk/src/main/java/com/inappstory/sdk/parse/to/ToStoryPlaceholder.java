package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.StoryPlaceholder;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;


public final class ToStoryPlaceholder extends ToModelParser<StoryPlaceholder> {
    public ToStoryPlaceholder(JSONObject value) {
        super(value);
    }

    @Override
    protected StoryPlaceholder parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            StoryPlaceholder storyPlaceholder = new StoryPlaceholder();
            storyPlaceholder.name = jsonObj.getString(
                    ModelFields.StoryPlaceholder.name
            );
            storyPlaceholder.defaultVal = jsonObj.optStringNullable(
                    ModelFields.StoryPlaceholder.defaultVal
            );
            return storyPlaceholder;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "StoryPlaceholder";
    }
}
