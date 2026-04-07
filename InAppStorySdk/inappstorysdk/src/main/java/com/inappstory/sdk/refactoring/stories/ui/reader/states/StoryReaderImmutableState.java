package com.inappstory.sdk.refactoring.stories.ui.reader.states;

import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;

import java.util.List;

public class StoryReaderImmutableState {

    public String readerUniqueId() {
        return readerUniqueId;
    }

    public ContentType contentType() {
        return contentType;
    }

    public SourceType sourceType() {
        return sourceType;
    }

    public String feed() {
        return feed;
    }

    public List<String> storiesIds() {
        return storiesIds;
    }

    public RequestLocalParameters sessionParameters() {
        return requestLocalParameters;
    }

    private final RequestLocalParameters requestLocalParameters;

    private final String readerUniqueId;
    private final ContentType contentType;
    private final SourceType sourceType;
    private final String feed;
    private final List<String> storiesIds;


    public StoryReaderImmutableState(
            RequestLocalParameters requestLocalParameters,
            String readerUniqueId,
            List<String> storiesIds,
            ContentType contentType,
            SourceType sourceType,
            String feed
    ) {
        this.readerUniqueId = readerUniqueId;
        this.requestLocalParameters = requestLocalParameters;
        this.contentType = contentType;
        this.sourceType = sourceType;
        this.feed = feed;
        this.storiesIds = storiesIds;
    }
}
