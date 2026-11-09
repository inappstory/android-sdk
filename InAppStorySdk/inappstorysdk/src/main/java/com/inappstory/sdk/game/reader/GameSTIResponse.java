package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameSTIResponse {
    @SerializedName(ModelFields.GameSTIResponse.id)
    public String id;
    @SerializedName(ModelFields.GameSTIResponse.response)
    public String response;
}
