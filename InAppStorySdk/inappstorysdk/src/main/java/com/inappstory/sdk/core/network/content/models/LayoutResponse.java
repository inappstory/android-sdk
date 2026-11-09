package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class LayoutResponse {
    @Required
    @SerializedName(ModelFields.LayoutResponse.layout)
    public String layout;
    @SerializedName(ModelFields.LayoutResponse.lastUpdatedAt)
    public String lastUpdatedAt;
}
