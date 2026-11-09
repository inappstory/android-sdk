package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class InGameResourceDownloadResultObj {
    @SerializedName(ModelFields.InGameResourceDownloadResultObj.result)
    public boolean result;
    @SerializedName(ModelFields.InGameResourceDownloadResultObj.reason)
    public String reason;
}
