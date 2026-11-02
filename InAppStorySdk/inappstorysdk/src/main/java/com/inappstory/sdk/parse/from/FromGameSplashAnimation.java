package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.stories.api.models.GameSplashAnimation;

import org.json.JSONObject;

public class FromGameSplashAnimation extends ModelToStringParser<GameSplashAnimation> {
    public FromGameSplashAnimation(GameSplashAnimation model) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put("url", model.url);
        object.put("size", model.size);
        object.put("durationMinimalMs", model.durationMinimalMs);
        object.put("sha1", model.sha1);
        return object;
    }
}
