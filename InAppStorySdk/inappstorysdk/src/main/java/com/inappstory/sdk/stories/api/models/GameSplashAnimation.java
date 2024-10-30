package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.core.dataholders.models.IDownloadResource;

public class GameSplashAnimation implements IDownloadResource {
    @SerializedName("url")
    public String url;
    @SerializedName("size")
    public Long size;
    @SerializedName("sha1")
    public String sha1;
    @SerializedName("durationMinimalMs")
    public Long durationMinimalMs;

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
