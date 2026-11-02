package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.cache.InGameResourceDownloadResultObj;

import org.json.JSONObject;

public class FromInGameResourceDownloadResultObj extends
        ModelToJSONObjectParser<InGameResourceDownloadResultObj> {
    public FromInGameResourceDownloadResultObj(
            InGameResourceDownloadResultObj model
    ) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put("result", model.result);
        object.put("reason", model.reason);
        return object;
    }
}
