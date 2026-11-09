package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.cache.InGameResourceDownloadResultObj;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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
        object.put(ModelFields.InGameResourceDownloadResultObj.result, model.result);
        object.put(ModelFields.InGameResourceDownloadResultObj.reason, model.reason);
        return object;
    }
}
