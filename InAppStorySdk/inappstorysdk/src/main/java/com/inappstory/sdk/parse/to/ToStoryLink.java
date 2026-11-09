package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.StoryLink;

import org.json.JSONObject;


public final class ToStoryLink extends ToModelParser<StoryLink> {
    public ToStoryLink(JSONObject value) {
        super(value);
    }

    @Override
    public StoryLink parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            StoryLink link = new StoryLink();
            link.target = jsonObj.optStringNullable(ModelFields.StoryLink.target);
            link.type = jsonObj.optStringNullable(ModelFields.StoryLink.type);
            return link;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "StoryLink";
    }
}
