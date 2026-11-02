package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.game.reader.GameFinishStoryOptions;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToGameFinishStoryOptions extends ToModelParser<GameFinishStoryOptions> {
    public ToGameFinishStoryOptions(JSONObject value) {
        super(value);
    }

    @Override
    public GameFinishStoryOptions parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameFinishStoryOptions options = new GameFinishStoryOptions();
            options.id = jsonObj.getString("id");
            options.slideIndex = jsonObj.optInt("slideIndex");
            return options;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameFinishStoryOptions";
    }
}
