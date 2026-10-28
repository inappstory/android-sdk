package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public final class ToImageList extends ToListParser<Image, ToImage> {
    @Override
    protected ToImage createParserInstance(JSONObject object) {
        return new ToImage(object);
    }

    public ToImageList(JSONArray value) {
        super(value);
    }
}
