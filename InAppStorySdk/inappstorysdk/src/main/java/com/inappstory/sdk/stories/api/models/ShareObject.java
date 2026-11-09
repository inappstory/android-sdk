package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

/**
 * Created by paperrose on 19.02.2018.
 */

public class ShareObject {
    @SerializedName(ModelFields.ShareObject.url)
    public String url;

    @SerializedName(ModelFields.ShareObject.title)
    public String title;

    @SerializedName(ModelFields.ShareObject.description)
    public String description;

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
