package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.reader.GameFinishOptions;
import com.inappstory.sdk.game.reader.GameLoadedConfig;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;


public final class ToGameLoadedConfig extends ToModelParser<GameLoadedConfig> {
    public ToGameLoadedConfig(String value) {
        super(value);
    }



    @Override
    protected GameLoadedConfig parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            GameLoadedConfig finishOptions = new GameLoadedConfig();
            finishOptions.backGesture = jsonObj.optBoolean(
                    ModelFields.GameLoadedConfig.backGesture
            );
            finishOptions.showClose = jsonObj.optBoolean(
                    ModelFields.GameLoadedConfig.showClose, true
            );
            return finishOptions;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameLoadedConfig";
    }
}
