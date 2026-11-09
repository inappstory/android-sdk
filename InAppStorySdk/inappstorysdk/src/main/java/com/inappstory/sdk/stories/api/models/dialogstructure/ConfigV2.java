package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class ConfigV2 implements Serializable {
    @SerializedName(ModelFields.ConfigV2.factor)
    public float factor;
    @SerializedName(ModelFields.ConfigV2.main)
    public MainV2 main;
}
