package com.inappstory.sdk.share;

import com.inappstory.sdk.network.SerializedName;

public class JSShareFile {
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
