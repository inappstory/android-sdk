package com.inappstory.sdk.inappmessage.domain.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class IAMReaderSlideStatObject {
    @SerializedName("si")
    public int slideIndex;
    @SerializedName("d")
    public long duration;

    public IAMReaderSlideStatObject(int slideIndex, long duration) {
        this.slideIndex = slideIndex;
        this.duration = duration;
    }
}
