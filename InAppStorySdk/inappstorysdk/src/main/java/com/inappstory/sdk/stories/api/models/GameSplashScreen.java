package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.core.network.annotations.models.SerializedName;

public class GameSplashScreen {
    @SerializedName("url")
    public String url;
    @SerializedName("size")
    public Long size;
    @SerializedName("sha1")
    public String sha1;
}
