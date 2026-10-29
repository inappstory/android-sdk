package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.InAppMessage;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToInAppMessageList extends ToListParser<InAppMessage, ToInAppMessage> {
    @Override
    protected ToInAppMessage createParserInstance(JSONObject object) {
        return new ToInAppMessage(object);
    }

    public ToInAppMessageList(JSONArray value) {
        super(value);
    }
}
