package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class InAppMessageEvent {
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("priority")
    public int priority;
}
