package com.inappstory.sdk.inner.share;

import com.inappstory.sdk.core.network.annotations.models.SerializedName;

public class InnerShareFile {
    public String getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @SerializedName("file")
    public String file;
    @SerializedName("name")
    public String name;
    @SerializedName("type")
    public String type;
}
