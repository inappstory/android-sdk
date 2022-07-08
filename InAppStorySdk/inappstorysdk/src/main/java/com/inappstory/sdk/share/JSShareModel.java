package com.inappstory.sdk.share;

import com.inappstory.sdk.network.SerializedName;

import java.util.ArrayList;

public class JSShareModel {
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

    public ArrayList<JSShareFile> getFiles() {
        return files != null ? files : new ArrayList<JSShareFile>();
    }

    @SerializedName("files")
    public ArrayList<JSShareFile> files;
}
