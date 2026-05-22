package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class LayoutResponse {
    @SerializedName("layout")
    public String layout;
    @SerializedName("last_updated_at")
    public String lastUpdatedAt;
}
