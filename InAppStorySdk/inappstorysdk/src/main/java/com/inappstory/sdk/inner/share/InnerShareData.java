package com.inappstory.sdk.inner.share;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.ArrayList;

public class InnerShareData {
    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String getPayload() {
        return payload;
    }

    public String getUrl() {
        return url;
    }

    @SerializedName("text")
    public String text;
    @SerializedName("title")
    public String title;
    @SerializedName("payload")
    public String payload;
    @SerializedName("url")
    public String url;

    public ArrayList<InnerShareFile> getFiles() {
        return files != null ? files : new ArrayList<InnerShareFile>();
    }

    @SerializedName("files")
    public ArrayList<InnerShareFile> files;
}
