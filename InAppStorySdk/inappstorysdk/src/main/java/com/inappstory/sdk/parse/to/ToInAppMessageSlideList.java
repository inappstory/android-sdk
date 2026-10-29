package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.InAppMessageSlide;
import com.inappstory.sdk.core.network.content.models.StorySlide;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToInAppMessageSlideList extends ToListParser<InAppMessageSlide, ToInAppMessageSlide> {
    @Override
    protected ToInAppMessageSlide createParserInstance(JSONObject object) {
        return new ToInAppMessageSlide(object);
    }

    public ToInAppMessageSlideList(JSONArray value) {
        super(value);
    }
}
