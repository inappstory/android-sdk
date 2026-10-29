package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.StoryPlaceholder;
import com.inappstory.sdk.parse.ExtendedJsonObject;

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
            storyPlaceholder.name = jsonObj.getString("name");
            storyPlaceholder.defaultVal = jsonObj.optStringNullable("default_value");
            return storyPlaceholder;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "StoryPlaceholder";
    }
}
