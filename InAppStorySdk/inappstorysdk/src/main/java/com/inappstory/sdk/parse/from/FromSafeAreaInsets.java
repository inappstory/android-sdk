package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.SafeAreaInsets;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;

import org.json.JSONObject;

public class FromSafeAreaInsets extends ModelToStringParser<SafeAreaInsets> {
    public FromSafeAreaInsets(SafeAreaInsets model) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put(ModelFields.SafeAreaInsets.top, model.top);
        object.put(ModelFields.SafeAreaInsets.bottom, model.bottom);
        object.put(ModelFields.SafeAreaInsets.left, model.left);
        object.put(ModelFields.SafeAreaInsets.right, model.right);
        return object;
    }
}
