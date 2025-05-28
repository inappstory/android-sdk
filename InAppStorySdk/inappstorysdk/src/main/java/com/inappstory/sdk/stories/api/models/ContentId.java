package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.inappmessage.domain.stedata.STEData;

public class ContentId implements STEData {
    private final String id;

    public String id() {
        return id;
    }

    public ContentId(String id) {
        this.id = id;
    }
}
