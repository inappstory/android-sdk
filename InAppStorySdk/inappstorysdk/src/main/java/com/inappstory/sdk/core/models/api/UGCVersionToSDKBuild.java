package com.inappstory.sdk.core.models.api;

import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

public class UGCVersionToSDKBuild {
    @SerializedName("minBuild")
    public int minBuild;
    @SerializedName("editor")
    public String editor;
}
