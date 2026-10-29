package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.stories.api.models.GameArchiveItem;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToGameArchiveItemList extends ToListParser<GameArchiveItem, ToGameArchiveItem> {
    @Override
    protected ToGameArchiveItem createParserInstance(JSONObject object) {
        return new ToGameArchiveItem(object);
    }

    public ToGameArchiveItemList(JSONArray value) {
        super(value);
    }
}
