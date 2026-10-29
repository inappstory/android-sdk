package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.GameSplashAnimation;

import org.json.JSONObject;


public final class ToGameSplashAnimation extends ToModelParser<GameSplashAnimation> {
    public ToGameSplashAnimation(JSONObject value) {
        super(value);
    }

    @Override
    protected GameSplashAnimation parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameSplashAnimation gameSplashAnimation = new GameSplashAnimation();
            gameSplashAnimation.url = jsonObj.getString("url");
            gameSplashAnimation.sha1 = jsonObj.optStringNullable("sha1");
            gameSplashAnimation.size = jsonObj.optLongNullable("size");
            gameSplashAnimation.durationMinimalMs = jsonObj.optLongNullable("durationMinimalMs");
            return gameSplashAnimation;
        } catch (Exception e) {
            return null;
        }
    }
}
