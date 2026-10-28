package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.ContentResource;
import com.inappstory.sdk.core.network.content.models.Image;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToContentResourceList extends ToListParser<ContentResource, ToContentResource> {
    @Override
    protected ToContentResource createParserInstance(JSONObject object) {
        return new ToContentResource(object);
    }

    public ToContentResourceList(JSONArray value) {
        super(value);
    }
}
