package com.inappstory.sdk.refactoring.session.data.network;

import com.inappstory.sdk.network.annotations.models.SerializedName;


public class NSessionPlaceholder {
    @SerializedName(NSessionPlaceholderResponseFields.name)
    public String name;
    @SerializedName(NSessionPlaceholderResponseFields.defaultVal)
    public String defaultVal;
}
