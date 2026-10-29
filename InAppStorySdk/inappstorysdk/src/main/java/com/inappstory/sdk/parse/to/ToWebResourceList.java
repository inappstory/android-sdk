package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.stories.api.models.WebResource;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToWebResourceList extends ToListParser<WebResource, ToWebResource> {
    @Override
    protected ToWebResource createParserInstance(JSONObject object) {
        return new ToWebResource(object);
    }

    public ToWebResourceList(JSONArray value) {
        super(value);
    }
}
