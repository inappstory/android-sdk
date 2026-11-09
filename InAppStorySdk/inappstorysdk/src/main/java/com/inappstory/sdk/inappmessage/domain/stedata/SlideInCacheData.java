package com.inappstory.sdk.inappmessage.domain.stedata;


import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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

    @SerializedName(ModelFields.SlideInCacheData.index)
    public int index;
    @SerializedName(ModelFields.SlideInCacheData.status)
    public int status;
}
