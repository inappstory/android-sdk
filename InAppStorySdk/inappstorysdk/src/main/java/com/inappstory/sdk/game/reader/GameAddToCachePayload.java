package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.WebResource;

import java.io.Serializable;

public class GameAddToCachePayload implements Serializable {
    @SerializedName(ModelFields.GameAddToCachePayload.id)
    public String id;
    @SerializedName(ModelFields.GameAddToCachePayload.cb)
    public String cb;
    @SerializedName(ModelFields.GameAddToCachePayload.webResource)
    public WebResource webResource;
}