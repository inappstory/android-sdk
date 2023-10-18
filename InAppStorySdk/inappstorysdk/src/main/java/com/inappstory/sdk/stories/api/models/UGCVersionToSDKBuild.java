package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.core.network.annotations.models.SerializedName;

public class UGCVersionToSDKBuild {
    @SerializedName("minBuild")
    public int minBuild;
    @SerializedName("editor")
    public String editor;
}
