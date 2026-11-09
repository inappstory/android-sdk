package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class CenterStructure implements Serializable {
    @SerializedName(ModelFields.CenterStructure.x)
    public float x;
    @SerializedName(ModelFields.CenterStructure.y)
    public float y;

    public CenterStructure() {}

    public CenterStructure(float x, float y) {
        this.x = x;
        this.y = y;
    }

}