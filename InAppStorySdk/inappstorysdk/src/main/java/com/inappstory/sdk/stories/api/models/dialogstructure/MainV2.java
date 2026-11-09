package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class MainV2 implements Serializable {
    @SerializedName(ModelFields.MainV2.background)
    public BackgroundStructure background;
    @SerializedName(ModelFields.MainV2.border)
    public BorderStructure border;
    @SerializedName(ModelFields.MainV2.padding)
    public PaddingStructure padding;
    @SerializedName(ModelFields.MainV2.input)
    public InputStructure input;
    @SerializedName(ModelFields.MainV2.button)
    public ButtonStructure button;
    @SerializedName(ModelFields.MainV2.question)
    public QuestionStructure question;
}
