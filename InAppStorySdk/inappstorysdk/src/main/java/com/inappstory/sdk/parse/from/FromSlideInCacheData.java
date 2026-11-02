package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.inappmessage.domain.stedata.SlideInCacheData;

import org.json.JSONObject;

public class FromSlideInCacheData extends
        ModelToStringParser<SlideInCacheData> {
    public FromSlideInCacheData(
            SlideInCacheData model
    ) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put("index", model.index);
        object.put("status", model.status);
        return object;
    }
}
