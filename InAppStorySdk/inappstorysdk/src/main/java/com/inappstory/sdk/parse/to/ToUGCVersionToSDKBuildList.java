package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.stories.api.models.UGCVersionToSDKBuild;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToUGCVersionToSDKBuildList extends
        ToListParser<UGCVersionToSDKBuild, ToUGCVersionToSDKBuild> {
    @Override
    protected ToUGCVersionToSDKBuild createParserInstance(JSONObject object) {
        return new ToUGCVersionToSDKBuild(object);
    }

    public ToUGCVersionToSDKBuildList(JSONArray value) {
        super(value);
    }
}
