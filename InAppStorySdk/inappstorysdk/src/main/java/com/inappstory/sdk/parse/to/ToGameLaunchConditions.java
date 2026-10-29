package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.game.reader.GameScreenOptions;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.GameLaunchConditions;

import org.json.JSONObject;


public final class ToGameLaunchConditions extends ToModelParser<GameLaunchConditions> {
    public ToGameLaunchConditions(JSONObject value) {
        super(value);
    }

    @Override
    protected GameLaunchConditions parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameLaunchConditions gameLaunchConditions = new GameLaunchConditions();
            gameLaunchConditions.minWebViewVersion =
                    new ToGameLCMinWebViewVersion(
                            jsonObj.optJSONObject("minWebViewVersion")
                    ).parse();
            return gameLaunchConditions;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    protected String getClassName() {
        return "GameLaunchConditions";
    }
}
