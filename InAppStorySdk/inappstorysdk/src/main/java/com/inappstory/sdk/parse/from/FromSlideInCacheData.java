package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.inappmessage.domain.stedata.SlideInCacheData;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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
        object.put(ModelFields.SlideInCacheData.index, model.index);
        object.put(ModelFields.SlideInCacheData.status, model.status);
        return object;
    }
}
