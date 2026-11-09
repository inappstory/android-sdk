package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameLCMinWebViewVersion;

import org.json.JSONObject;

public final class ToGameLCMinWebViewVersion extends ToModelParser<GameLCMinWebViewVersion> {
    public ToGameLCMinWebViewVersion(JSONObject value) {
        super(value);
    }

    @Override
    protected GameLCMinWebViewVersion parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameLCMinWebViewVersion minWebViewVersion  = new GameLCMinWebViewVersion();
            minWebViewVersion.minVersion = jsonObj.getInt(
                    ModelFields.GameLCMinWebViewVersion.minVersion
            );
            minWebViewVersion.message = jsonObj.optString(
                    ModelFields.GameLCMinWebViewVersion.message
            );
            minWebViewVersion.loggerMessage = jsonObj.optString(
                    ModelFields.GameLCMinWebViewVersion.loggerMessage
            );
            return minWebViewVersion;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameLCMinWebViewVersion";
    }
}
