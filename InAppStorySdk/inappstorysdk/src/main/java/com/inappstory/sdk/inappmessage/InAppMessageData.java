package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ContentData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;

public class InAppMessageData extends ContentData {
    private int id;
    private String event;

    private String title;
    private InAppMessageType messageType;

    public InAppMessageData(
            int id,
            String campaignName,
            String event,
            SourceType sourceType,
            InAppMessageType messageType
    ) {
        super(sourceType, ContentType.IN_APP_MESSAGE);
        this.id = id;
        this.title = campaignName;
        this.event = event;
        this.messageType = messageType;
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

    public InAppMessageType messageType() {
        return messageType;
    }

    @Override
    public String toString() {
        return "IAMData{" +
                "id=" + id() +
                ", title='" + title() + '\'' +
                ", event='" + event() + '\'' +
                ", sourceType='" + sourceType().name() + '\'' +
                ", messageType='" + messageType().name() + '\'' +
                '}';
    }
}
