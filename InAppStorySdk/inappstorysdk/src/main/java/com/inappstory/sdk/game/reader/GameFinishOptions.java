package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameFinishOptions {
    @SerializedName(ModelFields.GameFinishOptions.openUrl)
    public String openUrl;
    @SerializedName(ModelFields.GameFinishOptions.openStory)
    public GameFinishStoryOptions openStory;
    @SerializedName(ModelFields.GameFinishOptions.openGameInstance)
    public GameFinishGameOptions openGameInstance;
}