package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.SafeAreaInsets;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;

import org.json.JSONObject;

public class FromSafeAreaInsets extends ModelToStringParser<SafeAreaInsets> {
    public FromSafeAreaInsets(SafeAreaInsets model) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put("top", model.top);
        object.put("bottom", model.bottom);
        object.put("left", model.left);
        object.put("right", model.right);
        return object;
    }
}
