package com.inappstory.sdk.inner.share;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.util.ArrayList;

public class InnerShareData {
    public String getText() {
        return text;
    }
    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getPayload() {
        return payload;
    }

    @SerializedName(ModelFields.InnerShareData.text)
    public String text;
    @SerializedName(ModelFields.InnerShareData.title)
    public String title;
    @SerializedName(ModelFields.InnerShareData.payload)
    public String payload;
    @SerializedName(ModelFields.InnerShareData.url)
    public String url;

    public ArrayList<InnerShareFile> getFiles() {
        return files != null ? files : new ArrayList<InnerShareFile>();
    }

    @SerializedName("files")
    public ArrayList<InnerShareFile> files;
}
