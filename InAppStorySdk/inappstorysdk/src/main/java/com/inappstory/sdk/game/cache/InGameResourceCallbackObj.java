package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class InGameResourceCallbackObj {
    @SerializedName(ModelFields.InGameResourceCallbackObj.id)
    public String id;
    @SerializedName(ModelFields.InGameResourceCallbackObj.response)
    public InGameResourceDownloadResultObj response;
}
