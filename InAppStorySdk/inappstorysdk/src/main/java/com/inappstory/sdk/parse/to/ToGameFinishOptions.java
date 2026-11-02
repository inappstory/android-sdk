package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.reader.GameAcceleratorOptions;
import com.inappstory.sdk.game.reader.GameFinishOptions;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToGameFinishOptions extends ToModelParser<GameFinishOptions> {
    public ToGameFinishOptions(String value) {
        super(value);
    }



    @Override
    protected GameFinishOptions parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            GameFinishOptions finishOptions = new GameFinishOptions();
            finishOptions.openUrl = jsonObj.optStringNullable("openUrl");
            finishOptions.openGameInstance = new ToGameFinishGameOptions(
                    jsonObj.optJSONObject("openGameInstance")
            ).parse();
            finishOptions.openStory = new ToGameFinishStoryOptions(
                    jsonObj.optJSONObject("openStory")
            ).parse();

            return finishOptions;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameFinishOptions";
    }
}
