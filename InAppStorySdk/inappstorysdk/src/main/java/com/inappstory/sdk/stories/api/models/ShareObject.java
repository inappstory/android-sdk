package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.network.SerializedName;

/**
 * Created by paperrose on 19.02.2018.
 */

public class ShareObject {
    @SerializedName("url")
    String url;

    @SerializedName("title")
    String title;

    @SerializedName("payload")
    String payload;

    public String getUrl() {
        return url;
    }

    public String getPayload() {
        return payload;
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
