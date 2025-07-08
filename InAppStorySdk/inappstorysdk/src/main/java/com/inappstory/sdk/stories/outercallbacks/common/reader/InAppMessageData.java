package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.stories.api.models.ContentType;

public class InAppMessageData extends ContentData {
    private int id;
    private String event;

    private String title;

    public InAppMessageData(
            int id,
            String campaignName,
            SourceType sourceType
    ) {
        super(sourceType, ContentType.IN_APP_MESSAGE);
        this.id = id;
        this.title = campaignName;
    }
    public InAppMessageData(
            int id,
            String campaignName,
            String event,
            SourceType sourceType
    ) {
        super(sourceType, ContentType.IN_APP_MESSAGE);
        this.id = id;
        this.title = campaignName;
        this.event = event;
    }

    public int id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String event() {
        return event;
    }

    @Override
    public String toString() {
        return "IAMData{" +
                "id=" + id() +
                ", title='" + title() + '\'' +
                ", event='" + event() + '\'' +
                ", sourceType='" + sourceType().name() + '\'' +
                '}';
    }
}
