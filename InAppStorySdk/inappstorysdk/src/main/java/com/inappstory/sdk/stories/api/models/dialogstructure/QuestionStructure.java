package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class QuestionStructure implements Serializable {
    @SerializedName(ModelFields.QuestionStructure.padding)
    public PaddingStructure padding;
    @SerializedName(ModelFields.QuestionStructure.text)
    public TextStructure text;
}
