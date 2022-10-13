package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.SerializedName;

public class SendStoryDataObject {
    @SerializedName("data")
    String data;

    public SendStoryDataObject(String data) {
        this.data = data;
    }
}
