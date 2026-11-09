package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.reader.GameAcceleratorOptions;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.UrlObject;

import org.json.JSONObject;


public final class ToGameAcceleratorOptions extends ToModelParser<GameAcceleratorOptions> {
    public ToGameAcceleratorOptions(String value) {
        super(value);
    }



    @Override
    protected GameAcceleratorOptions parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            GameAcceleratorOptions acceleratorOptions = new GameAcceleratorOptions();
            acceleratorOptions.frequency = jsonObj.optDouble(
                    ModelFields.GameAcceleratorOptions.frequency
            );
            return acceleratorOptions;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameAcceleratorOptions";
    }
}
