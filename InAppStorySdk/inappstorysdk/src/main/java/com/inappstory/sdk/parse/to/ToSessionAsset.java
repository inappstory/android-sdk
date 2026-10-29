package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.core.network.content.models.StoryPlaceholder;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToSessionAsset extends ToModelParser<SessionAsset> {
    public ToSessionAsset(JSONObject value) {
        super(value);
    }

    @Override
    protected SessionAsset parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            SessionAsset asset = new SessionAsset();
            asset.url = jsonObj.getString("url");
            asset.sha1 = jsonObj.optStringNullable("sha1");
            asset.type = jsonObj.optStringNullable("type");
            asset.replaceKey = jsonObj.optStringNullable("key");
            asset.filename = jsonObj.optStringNullable("filename");
            asset.format = jsonObj.optStringNullable("format");
            asset.mimeType = jsonObj.optStringNullable("mimeType");
            asset.purpose = jsonObj.optStringNullable("purpose");
            asset.size = jsonObj.optLong("size");
            return asset;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "SessionAsset";
    }
}
