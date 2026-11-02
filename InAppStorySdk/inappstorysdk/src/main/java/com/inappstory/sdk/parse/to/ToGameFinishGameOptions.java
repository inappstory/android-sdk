package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.game.reader.GameFinishGameOptions;
import com.inappstory.sdk.game.reader.GameFinishStoryOptions;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToGameFinishGameOptions extends ToModelParser<GameFinishGameOptions> {
    public ToGameFinishGameOptions(JSONObject value) {
        super(value);
    }

    @Override
    public GameFinishGameOptions parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameFinishGameOptions options = new GameFinishGameOptions();
            options.id = jsonObj.getString("id");
            return options;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameFinishGameOptions";
    }
}
