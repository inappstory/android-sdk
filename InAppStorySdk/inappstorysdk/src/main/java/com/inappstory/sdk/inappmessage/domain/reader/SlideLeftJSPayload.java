package com.inappstory.sdk.inappmessage.domain.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class SlideLeftJSPayload {
    @SerializedName(ModelFields.SlideLeftJSPayload.index)
    public int index;
    @SerializedName(ModelFields.SlideLeftJSPayload.cardId)
    public int cardId;
}
