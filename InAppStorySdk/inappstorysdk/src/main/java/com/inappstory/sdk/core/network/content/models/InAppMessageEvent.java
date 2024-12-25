package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class InAppMessageEvent {
    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;
}
