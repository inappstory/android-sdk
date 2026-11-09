package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.core.data.IDownloadResource;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameSplashScreen implements IDownloadResource {
    @Required
    @SerializedName(ModelFields.GameSplashScreen.url)
    public String url;
    @SerializedName(ModelFields.GameSplashScreen.size)
    public Long size;
    @SerializedName(ModelFields.GameSplashScreen.sha1)
    public String sha1;

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
