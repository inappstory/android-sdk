package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameSTIConfigInputData {
    @SerializedName(ModelFields.GameSTIConfigInputData.placeholder)
    public String placeholder;
    @SerializedName(ModelFields.GameSTIConfigInputData.type)
    public String type;
}
