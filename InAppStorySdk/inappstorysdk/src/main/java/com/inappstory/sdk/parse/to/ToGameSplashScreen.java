package com.inappstory.sdk.parse.to;


import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
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
            gameSplashScreen.url = jsonObj.getString(ModelFields.GameSplashScreen.url);
            gameSplashScreen.size = jsonObj.optLongNullable(ModelFields.GameSplashScreen.size);
            gameSplashScreen.sha1 = jsonObj.optStringNullable(ModelFields.GameSplashScreen.sha1);
            return gameSplashScreen;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameSplashScreen";
    }
}
