package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.core.dataholders.models.IResource;

/**
 * Created by paperrose on 19.02.2018.
 */

public class ResourceMapping implements IResource {
    @SerializedName("url")
    public String url;
    @SerializedName("key")
    public String key;
    @SerializedName("type")
    public String type;
    @SerializedName("purpose")
    public String purpose;
    @SerializedName("filename")
    public String filename;

    @SerializedName("slide_index")
    public Integer index;

    @SerializedName("range_start")
    public Long rangeStart;

    @SerializedName("range_end")
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
