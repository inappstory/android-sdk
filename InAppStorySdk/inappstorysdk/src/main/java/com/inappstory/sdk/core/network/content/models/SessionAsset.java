package com.inappstory.sdk.core.network.content.models;



import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.util.Objects;

/**
 * Created by paperrose on 19.02.2018.
 */

public class SessionAsset {
    @Required
    @SerializedName(ModelFields.SessionAsset.url)
    public String url;
    @SerializedName(ModelFields.SessionAsset.size)
    public long size;
    @SerializedName(ModelFields.SessionAsset.sha1)
    public String sha1;
    @SerializedName(ModelFields.SessionAsset.type)
    public String type;
    @SerializedName(ModelFields.SessionAsset.replaceKey)
    public String replaceKey;
    @SerializedName(ModelFields.SessionAsset.filename)
    public String filename;
    @SerializedName(ModelFields.SessionAsset.format)
    public String format;
    @SerializedName(ModelFields.SessionAsset.mimeType)
    public String mimeType;
    @SerializedName(ModelFields.SessionAsset.purpose)
    public String purpose;

    public boolean isMainAsset() {
        return purpose != null &&
                (purpose.equals("common") || purpose.equals("font") || purpose.equals("animation") || purpose.equals("cardApi"));
    }

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
                Objects.equals(purpose, that.purpose) &&
                Objects.equals(mimeType, that.mimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, size, sha1, type, replaceKey, filename, format, purpose, mimeType);
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
