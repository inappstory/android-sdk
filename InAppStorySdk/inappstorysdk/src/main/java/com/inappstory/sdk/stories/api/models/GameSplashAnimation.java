package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.interfaces.IDownloadResource;

public class GameSplashAnimation implements IDownloadResource {
    @SerializedName("url")
    public String url;
    @SerializedName("size")
    public Long size;
    @SerializedName("sha1")
    public String sha1;
    @SerializedName("loop")
    public boolean loop;

    @Override
    public String url() {
        return url;
    }

    @Override
    public long size() {
        return size != null ? size : 0;
    }

    @Override
    public String sha1() {
        return sha1;
    }
}
