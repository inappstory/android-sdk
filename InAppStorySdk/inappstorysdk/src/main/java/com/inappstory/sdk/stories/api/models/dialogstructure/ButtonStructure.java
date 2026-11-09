package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class ButtonStructure implements Serializable {
    @SerializedName(ModelFields.ButtonStructure.background)
    public BackgroundStructure background;
    @SerializedName(ModelFields.ButtonStructure.border)
    public BorderStructure border;
    @SerializedName(ModelFields.ButtonStructure.text)
    public TextStructure text;
    @SerializedName(ModelFields.ButtonStructure.padding)
    public PaddingStructure padding;
}