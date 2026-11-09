package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.core.network.content.models.StoryPlaceholder;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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
            asset.url = jsonObj.getString(
                    ModelFields.SessionAsset.url
            );
            asset.sha1 = jsonObj.optStringNullable(
                    ModelFields.SessionAsset.sha1
            );
            asset.type = jsonObj.optStringNullable(
                    ModelFields.SessionAsset.type
            );
            asset.replaceKey = jsonObj.optStringNullable(
                    ModelFields.SessionAsset.replaceKey
            );
            asset.filename = jsonObj.optStringNullable(
                    ModelFields.SessionAsset.filename
            );
            asset.format = jsonObj.optStringNullable(
                    ModelFields.SessionAsset.format
            );
            asset.mimeType = jsonObj.optStringNullable(
                    ModelFields.SessionAsset.mimeType
            );
            asset.purpose = jsonObj.optStringNullable(
                    ModelFields.SessionAsset.purpose
            );
            asset.size = jsonObj.optLong(
                    ModelFields.SessionAsset.size
            );
            return asset;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "SessionAsset";
    }
}
