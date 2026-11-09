package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameSTIConfigData {
    @SerializedName(ModelFields.GameSTIConfigData.title)
    public String title;
    @SerializedName(ModelFields.GameSTIConfigData.button)
    public String button;
    @SerializedName(ModelFields.GameSTIConfigData.input)
    public GameSTIConfigInputData input;
}
