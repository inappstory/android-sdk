package com.inappstory.sdk.inappmessage.domain.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class IAMReaderSlideStatObject {
    @SerializedName(ModelFields.IAMReaderSlideStatObject.slideIndex)
    public int slideIndex;
    @SerializedName(ModelFields.IAMReaderSlideStatObject.duration)
    public long duration;

    public IAMReaderSlideStatObject(int slideIndex, long duration) {
        this.slideIndex = slideIndex;
        this.duration = duration;
    }
}
