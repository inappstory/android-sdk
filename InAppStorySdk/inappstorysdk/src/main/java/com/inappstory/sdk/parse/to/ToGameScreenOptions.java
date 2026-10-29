package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.reader.GameScreenOptions;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.GameCenterData;

import org.json.JSONObject;


public final class ToGameScreenOptions extends ToModelParser<GameScreenOptions> {
    public ToGameScreenOptions(JSONObject value) {
        super(value);
    }

    @Override
    protected GameScreenOptions parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameScreenOptions gameCenterData = new GameScreenOptions();
            gameCenterData.fullScreen = jsonObj.optBoolean("fullScreen");
            gameCenterData.screenOrientation = jsonObj.optStringNullable("screenOrientation");
            return gameCenterData;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "GameScreenOptions";
    }
}
