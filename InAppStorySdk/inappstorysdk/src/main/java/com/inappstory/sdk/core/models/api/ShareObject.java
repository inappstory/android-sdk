package com.inappstory.sdk.core.models.api;


import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

/**
 * Created by paperrose on 19.02.2018.
 */

public class ShareObject {
    @SerializedName("url")
    String url;

    @SerializedName("title")
    String title;

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
    String description;
}
