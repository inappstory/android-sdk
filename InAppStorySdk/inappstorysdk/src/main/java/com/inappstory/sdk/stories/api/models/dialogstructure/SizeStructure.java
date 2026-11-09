package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class SizeStructure implements Serializable {
    @SerializedName(ModelFields.SizeStructure.width)
    public float width;
    @SerializedName(ModelFields.SizeStructure.height)
    public float height;
    @SerializedName(ModelFields.SizeStructure.left)
    public float left;
    @SerializedName(ModelFields.SizeStructure.center)
    public CenterStructure center;
}