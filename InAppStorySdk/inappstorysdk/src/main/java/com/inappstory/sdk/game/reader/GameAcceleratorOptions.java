package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameAcceleratorOptions {
    @SerializedName(ModelFields.GameAcceleratorOptions.frequency)
    public double frequency;
}
