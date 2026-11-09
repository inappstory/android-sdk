package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class BorderStructure implements Serializable {
    @SerializedName(ModelFields.BorderStructure.radius)
    public float radius;
    @SerializedName(ModelFields.BorderStructure.width)
    public float width;
    @SerializedName(ModelFields.BorderStructure.color)
    public String color;
}