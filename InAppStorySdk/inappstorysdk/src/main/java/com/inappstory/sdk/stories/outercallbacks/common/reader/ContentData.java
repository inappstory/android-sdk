package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.stories.api.models.ContentType;

import java.io.Serializable;

public abstract class ContentData implements Serializable {
    private SourceType sourceType;
    private ContentType contentType;

    public ContentData(SourceType sourceType, ContentType contentType) {
        this.sourceType = sourceType;
        this.contentType = contentType;
    }

    public SourceType sourceType() {
        return sourceType;
    }

    public ContentType contentType() {
        return contentType;
    }
}
