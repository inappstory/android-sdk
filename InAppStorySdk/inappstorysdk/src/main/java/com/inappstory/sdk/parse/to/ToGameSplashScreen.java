package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.GameSplashScreen;

import org.json.JSONObject;


public final class ToGameSplashScreen extends ToModelParser<GameSplashScreen> {
    public ToGameSplashScreen(JSONObject value) {
        super(value);
    }

    @Override
    protected GameSplashScreen parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameSplashScreen gameSplashScreen = new GameSplashScreen();
            gameSplashScreen.url = jsonObj.getString("url");
            gameSplashScreen.size = jsonObj.optLongNullable("size");
            gameSplashScreen.sha1 = jsonObj.optStringNullable("sha1");
            return gameSplashScreen;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "GameSplashScreen";
    }
}
