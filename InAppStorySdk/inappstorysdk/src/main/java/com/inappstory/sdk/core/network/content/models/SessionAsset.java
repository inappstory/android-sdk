package com.inappstory.sdk.core.network.content.models;


import androidx.annotation.NonNull;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.Objects;

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
    @SerializedName("format")
    public String format;
    @SerializedName("mimeType")
    public String mimeType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionAsset)) return false;
        SessionAsset that = (SessionAsset) o;
        return size == that.size &&
                Objects.equals(url, that.url) &&
                Objects.equals(sha1, that.sha1) &&
                Objects.equals(type, that.type) &&
                Objects.equals(replaceKey, that.replaceKey) &&
                Objects.equals(filename, that.filename) &&
                Objects.equals(format, that.format) &&
                Objects.equals(mimeType, that.mimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, size, sha1, type, replaceKey, filename, format, mimeType);
    }

    @Override
    public String toString() {
        return "SessionAsset{" +
                "url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", replaceKey='" + replaceKey + '\'' +
                ", filename='" + filename + '\'' +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }
}
