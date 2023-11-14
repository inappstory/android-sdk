package com.inappstory.sdk.core.models.api;

import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

public class GameSplashScreen {
    @SerializedName("url")
    public String url;
    @SerializedName("size")
    public Long size;
    @SerializedName("sha1")
    public String sha1;
}
