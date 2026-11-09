package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class UGCVersionToSDKBuild {
    @SerializedName(ModelFields.UGCVersionToSDKBuild.minBuild)
    public int minBuild;
    @SerializedName(ModelFields.UGCVersionToSDKBuild.editor)
    public String editor;
}
