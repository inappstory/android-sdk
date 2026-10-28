package com.inappstory.sdk.inappmessage.domain.stedata;


import com.inappstory.sdk.network.annotations.models.SerializedName;

public class SlideInCacheData implements STEData {
    public int index() {
        return index;
    }

    public int status() {
        return status;
    }


    public SlideInCacheData index(int index) {
        this.index = index;
        return this;
    }

    public SlideInCacheData status(int status) {
        this.status = status;
        return this;
    }

    public SlideInCacheData() {
    }

    @SerializedName("index")
    public int index;
    @SerializedName("status")
    public int status;
}
