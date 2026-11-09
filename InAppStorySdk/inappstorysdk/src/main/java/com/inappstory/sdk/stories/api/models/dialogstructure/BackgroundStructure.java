package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class BackgroundStructure implements Serializable {
    @SerializedName(ModelFields.BackgroundStructure.color)
    public String color;
}