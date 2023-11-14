package com.inappstory.sdk.inner.share;

import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

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

    @SerializedName("text")
    public String text;
    @SerializedName("title")
    public String title;
    @SerializedName("payload")
    public String payload;

    public ArrayList<InnerShareFile> getFiles() {
        return files != null ? files : new ArrayList<InnerShareFile>();
    }

    @SerializedName("files")
    public ArrayList<InnerShareFile> files;
}
