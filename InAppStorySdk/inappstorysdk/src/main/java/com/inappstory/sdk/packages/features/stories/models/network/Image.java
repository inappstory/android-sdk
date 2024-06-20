package com.inappstory.sdk.packages.features.stories.models.network;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class Image {
    @SerializedName("url")
    public String url;

    @SerializedName("width")
    public int width;

    @SerializedName("height")
    public int height;

    @SerializedName("type")
    public String type;

    @SerializedName("expire")
    public int expire;
}
