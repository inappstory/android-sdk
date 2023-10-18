package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.core.network.annotations.models.SerializedName;

/**
 * Created by paperrose on 19.02.2018.
 */

public class ImagePlaceholderMappingObject {
    @SerializedName("url")
    public String url;
    @SerializedName("key")
    public String key;
    @SerializedName("type")
    public String type;

    @SerializedName("slide_index")
    public Integer index;

    public String getType() {
        return type;
    }
    public String getUrl() {
        return url;
    }
    public Integer getIndex() {
        return index;
    }

    public String getKey() {
        return key;
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
}
