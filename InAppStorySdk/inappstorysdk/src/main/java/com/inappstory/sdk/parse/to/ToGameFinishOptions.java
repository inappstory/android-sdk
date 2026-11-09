package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.reader.GameAcceleratorOptions;
import com.inappstory.sdk.game.reader.GameFinishOptions;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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
            finishOptions.openUrl = jsonObj.optStringNullable(
                    ModelFields.GameFinishOptions.openUrl
            );
            finishOptions.openGameInstance = new ToGameFinishGameOptions(jsonObj.optJSONObject(
                            ModelFields.GameFinishOptions.openGameInstance
            )).parse();
            finishOptions.openStory = new ToGameFinishStoryOptions(jsonObj.optJSONObject(
                    ModelFields.GameFinishOptions.openStory
            )).parse();

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
