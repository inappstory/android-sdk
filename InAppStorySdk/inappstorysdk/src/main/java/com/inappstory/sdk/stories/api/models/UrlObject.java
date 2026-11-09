package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class UrlObject {
    @SerializedName(ModelFields.UrlObject.url)
    public String url;
}