package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.StorySlide;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToStorySlideList extends ToListParser<StorySlide, ToStorySlide> {
    @Override
    protected ToStorySlide createParserInstance(JSONObject object) {
        return new ToStorySlide(object);
    }

    public ToStorySlideList(JSONArray value) {
        super(value);
    }
}
