package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.inappmessage.stedata.STEData;

import java.io.Serializable;

public class ContentId implements STEData {
    private final String id;

    public String id() {
        return id;
    }

    public ContentId(String id) {
        this.id = id;
    }
}
