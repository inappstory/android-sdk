package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.core.network.content.models.StoryPlaceholder;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToSessionAssetList extends ToListParser<SessionAsset, ToSessionAsset> {
    @Override
    protected ToSessionAsset createParserInstance(JSONObject object) {
        return new ToSessionAsset(object);
    }

    public ToSessionAssetList(JSONArray value) {
        super(value);
    }
}
