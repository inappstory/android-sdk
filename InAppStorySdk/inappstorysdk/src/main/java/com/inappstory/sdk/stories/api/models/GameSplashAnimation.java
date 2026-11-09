package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.core.data.IDownloadResource;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameSplashAnimation implements IDownloadResource {
    @Required
    @SerializedName(ModelFields.GameSplashAnimation.url)
    public String url;
    @SerializedName(ModelFields.GameSplashAnimation.size)
    public Long size;
    @SerializedName(ModelFields.GameSplashAnimation.sha1)
    public String sha1;
    @SerializedName(ModelFields.GameSplashAnimation.durationMinimalMs)
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
