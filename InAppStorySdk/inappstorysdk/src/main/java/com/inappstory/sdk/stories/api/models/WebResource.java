package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.core.data.IDownloadResource;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class WebResource implements Serializable, IDownloadResource {
    @SerializedName(ModelFields.WebResource.key)
    public String key;
    @SerializedName(ModelFields.WebResource.url)
    public String url;
    @SerializedName(ModelFields.WebResource.sha1)
    public String sha1;
    @SerializedName(ModelFields.WebResource.size)
    public long size;

    @Override
    public String url() {
        return url;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public String sha1() {
        return sha1;
    }

    public String uniqueKey() {
        if (sha1 != null) return url + " " + sha1;
        return url;
    }
}
