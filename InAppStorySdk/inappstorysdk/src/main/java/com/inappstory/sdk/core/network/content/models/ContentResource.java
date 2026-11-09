package com.inappstory.sdk.core.network.content.models;


import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

/**
 * Created by paperrose on 19.02.2018.
 */

public class ContentResource implements IResource {
    @SerializedName(ModelFields.ContentResource.url)
    public String url;
    @SerializedName(ModelFields.ContentResource.key)
    public String key;
    @SerializedName(ModelFields.ContentResource.type)
    public String type;
    @SerializedName(ModelFields.ContentResource.purpose)
    public String purpose;
    @SerializedName(ModelFields.ContentResource.filename)
    public String filename;
    @SerializedName(ModelFields.ContentResource.index)
    public Integer index;
    @SerializedName(ModelFields.ContentResource.rangeStart)
    public Long rangeStart;
    @SerializedName(ModelFields.ContentResource.rangeEnd)
    public Long rangeEnd;

    public String getType() {
        return type;
    }
    public String getUrl() {
        return url;
    }
    public int getIndex() {
        return index != null ? index : -1;
    }

    public String getKey() {
        return key;
    }

    public String getFileName() {
        return filename;
    }

    public String getPurpose() {
        return purpose != null ? purpose : "";
    }

    public long getRangeStart() {
        return rangeStart != null ? rangeStart : -1;
    }

    public long getRangeEnd() {
        return rangeEnd != null ? rangeEnd : -1;
    }

    @Override
    public int hashCode() {
        int res = 0;
        if (url != null) {
            res += url.hashCode();
        }
        if (key != null) {
            res += key.hashCode();
        }
        return res;
    }


    public static final String VOD = "vod";

}
