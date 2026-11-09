package com.inappstory.sdk.inappmessage.domain.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class ShowSlideJSPayload {
    @SerializedName(ModelFields.ShowSlideJSPayload.index)
    public int index;
    @SerializedName(ModelFields.ShowSlideJSPayload.cardId)
    public int cardId;
}
