package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.cache.InGameResourceCallbackObj;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameSplashAnimation;

import org.json.JSONObject;

public class FromInGameResourceCallbackObj extends ModelToStringParser<InGameResourceCallbackObj> {
    public FromInGameResourceCallbackObj(InGameResourceCallbackObj model) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put(ModelFields.InGameResourceCallbackObj.id, model.id);
        object.put(
                ModelFields.InGameResourceCallbackObj.response,
                new FromInGameResourceDownloadResultObj(model.response).parseToJSONObject()
        );
        return object;
    }
}
