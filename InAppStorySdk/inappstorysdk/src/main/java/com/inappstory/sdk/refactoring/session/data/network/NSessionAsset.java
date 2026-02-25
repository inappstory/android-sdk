package com.inappstory.sdk.refactoring.session.data.network;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class NSessionAsset {
    @SerializedName(NSessionAssetResponseFields.url)
    public String url;
    @SerializedName(NSessionAssetResponseFields.size)
    public long size;
    @SerializedName(NSessionAssetResponseFields.sha1)
    public String sha1;
    @SerializedName(NSessionAssetResponseFields.type)
    public String type;
    @SerializedName(NSessionAssetResponseFields.replaceKey)
    public String replaceKey;
    @SerializedName(NSessionAssetResponseFields.filename)
    public String filename;
    @SerializedName(NSessionAssetResponseFields.mimeType)
    public String mimeType;
}
