package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.network.annotations.models.SerializedName;

/**
 * Created by paperrose on 19.02.2018.
 */

public class ShareObject {
    @SerializedName("url")
    public String url;

    @SerializedName("title")
    public String title;

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @SerializedName("description")
    public String description;
}
