package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.core.data.IDownloadResource;

import java.io.Serializable;

public class WebResource implements Serializable, IDownloadResource {
    public String key;
    public String url;
    public String sha1;
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
}
