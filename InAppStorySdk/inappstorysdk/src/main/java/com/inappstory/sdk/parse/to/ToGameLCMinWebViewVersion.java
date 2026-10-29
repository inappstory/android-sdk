package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
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
            minWebViewVersion.minVersion = jsonObj.getInt("minVersion");
            minWebViewVersion.message = jsonObj.optString("message");
            minWebViewVersion.loggerMessage = jsonObj.optString("loggerMessage");
            return minWebViewVersion;
        } catch (Exception e) {
            return null;
        }
    }
}
