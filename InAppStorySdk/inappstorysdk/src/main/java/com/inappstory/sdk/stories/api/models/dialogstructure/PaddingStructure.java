package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class PaddingStructure implements Serializable {
    @SerializedName(ModelFields.PaddingStructure.top)
    public float top;
    @SerializedName(ModelFields.PaddingStructure.bottom)
    public float bottom;
    @SerializedName(ModelFields.PaddingStructure.left)
    public float left;
    @SerializedName(ModelFields.PaddingStructure.right)
    public float right;
}
