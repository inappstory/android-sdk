package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameLoadedConfig {
    @SerializedName(ModelFields.GameLoadedConfig.showClose)
    public boolean showClose = true;
    @SerializedName(ModelFields.GameLoadedConfig.backGesture)
    public boolean backGesture = false;
}
