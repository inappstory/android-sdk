package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.models.WebResource;

import java.io.Serializable;

public class GameAddToCachePayload implements Serializable {
    @SerializedName("id")
    public String id;
    @SerializedName("cb")
    public String cb;
    @SerializedName("resource")
    public WebResource webResource;
}