package com.inappstory.sdk.stories.api.models;


import androidx.annotation.NonNull;

import com.inappstory.sdk.network.annotations.models.SerializedName;

/**
 * Created by paperrose on 19.02.2018.
 */

public class SessionAsset {
    @SerializedName("url")
    public String url;
    @SerializedName("size")
    public long size;
    @SerializedName("sha1")
    public String sha1;
    @SerializedName("type")
    public String type;
    @SerializedName("key")
    public String replaceKey;
    @SerializedName("filename")
    public String filename;
    @SerializedName("mimeType")
    public String mimeType;

    @NonNull
    @Override
    public String toString() {
        return "SessionCacheObject {" + "\nUrl: " + url + "\nType: " + type + "\n}";
    }
}
