package com.inappstory.sdk.share;

import com.inappstory.sdk.network.SerializedName;

import java.util.ArrayList;

public class IASShareModel {
    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    @SerializedName("text")
    public String text;
    @SerializedName("title")
    public String title;

    public ArrayList<IASShareFile> getFiles() {
        return files != null ? files : new ArrayList<IASShareFile>();
    }

    @SerializedName("files")
    public ArrayList<IASShareFile> files;
}
