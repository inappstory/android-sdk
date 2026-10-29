package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.InAppMessageEvent;
import com.inappstory.sdk.core.network.content.models.InAppMessageSlide;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToInAppMessageEventList extends ToListParser<InAppMessageEvent, ToInAppMessageEvent> {
    @Override
    protected ToInAppMessageEvent createParserInstance(JSONObject object) {
        return new ToInAppMessageEvent(object);
    }

    public ToInAppMessageEventList(JSONArray value) {
        super(value);
    }
}
