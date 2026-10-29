package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.UGCVersionToSDKBuild;

import org.json.JSONObject;


public final class ToUGCVersionToSDKBuild extends ToModelParser<UGCVersionToSDKBuild> {
    public ToUGCVersionToSDKBuild(JSONObject value) {
        super(value);
    }

    @Override
    public UGCVersionToSDKBuild parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            UGCVersionToSDKBuild ugcVersionToSDKBuild = new UGCVersionToSDKBuild();
            ugcVersionToSDKBuild.minBuild = jsonObj.optInt("minBuild");
            ugcVersionToSDKBuild.editor = jsonObj.optStringNullable("editor");
            return ugcVersionToSDKBuild;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "UGCVersionToSDKBuild";
    }
}
