package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class GameScreenOptions implements Serializable {
    @SerializedName(ModelFields.GameScreenOptions.screenOrientation)
    public String screenOrientation;
    @SerializedName(ModelFields.GameScreenOptions.fullScreen)
    public boolean fullScreen;
}