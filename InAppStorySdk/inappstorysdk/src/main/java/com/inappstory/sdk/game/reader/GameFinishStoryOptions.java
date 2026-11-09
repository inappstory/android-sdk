package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameFinishStoryOptions {
    @SerializedName(ModelFields.GameFinishStoryOptions.id)
    public String id;
    @SerializedName(ModelFields.GameFinishStoryOptions.slideIndex)
    public int slideIndex;
}