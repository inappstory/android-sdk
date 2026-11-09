package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class WriteClipboardData {
    @SerializedName(ModelFields.WriteClipboardData.type)
    public String type;
    @SerializedName(ModelFields.WriteClipboardData.textValue)
    public String textValue;
}
