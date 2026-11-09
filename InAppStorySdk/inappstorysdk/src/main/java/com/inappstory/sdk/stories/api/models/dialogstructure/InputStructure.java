package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class InputStructure implements Serializable {
    @SerializedName(ModelFields.InputStructure.background)
    public BackgroundStructure background;
    @SerializedName(ModelFields.InputStructure.border)
    public BorderStructure border;
    @SerializedName(ModelFields.InputStructure.text)
    public TextStructure text;
    @SerializedName(ModelFields.InputStructure.padding)
    public PaddingStructure padding;
    @SerializedName(ModelFields.InputStructure.type)
    public String type;

    public int limit() {
        return text.limit;
    }

    public int maxLines() {
        return Math.max(text.maxLines, 1);
    }
}