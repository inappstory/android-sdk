package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.GameInstance;

import org.json.JSONObject;


public final class ToGameInstance extends ToModelParser<GameInstance> {
    public ToGameInstance(JSONObject value) {
        super(value);
    }

    @Override
    public GameInstance parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameInstance gameInstance = new GameInstance();
            gameInstance.id = jsonObj.getString("id");
            return gameInstance;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "GameInstance";
    }
}
