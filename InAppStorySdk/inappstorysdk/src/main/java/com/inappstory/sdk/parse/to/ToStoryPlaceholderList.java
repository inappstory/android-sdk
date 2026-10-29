package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.StoryPlaceholder;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToStoryPlaceholderList extends ToListParser<StoryPlaceholder, ToStoryPlaceholder> {
    @Override
    protected ToStoryPlaceholder createParserInstance(JSONObject object) {
        return new ToStoryPlaceholder(object);
    }

    public ToStoryPlaceholderList(JSONArray value) {
        super(value);
    }
}
