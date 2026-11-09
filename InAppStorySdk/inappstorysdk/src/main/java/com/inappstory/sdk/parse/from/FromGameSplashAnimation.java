package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameSplashAnimation;

import org.json.JSONObject;

public class FromGameSplashAnimation extends ModelToStringParser<GameSplashAnimation> {
    public FromGameSplashAnimation(GameSplashAnimation model) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put(ModelFields.GameSplashAnimation.url, model.url);
        object.put(ModelFields.GameSplashAnimation.size, model.size);
        object.put(ModelFields.GameSplashAnimation.durationMinimalMs, model.durationMinimalMs);
        object.put(ModelFields.GameSplashAnimation.sha1, model.sha1);
        return object;
    }
}
