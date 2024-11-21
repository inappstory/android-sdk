package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.stories.api.models.ContentType;

public class IAMData extends ContentData {
    private int id;

    private String title;

    public IAMData(
            int id,
            String campaignName,
            SourceType sourceType
    ) {
        super(sourceType, ContentType.IN_APP_MESSAGE);
        this.id = id;
        this.title = campaignName;
    }


    public int id() {
        return id;
    }

    public String title() {
        return title;
    }

    @Override
    public String toString() {
        return "IAMData{" +
                "id=" + id() +
                ", title='" + title() + '\'' +
                ", sourceType='" + sourceType().name() + '\'' +
                '}';
    }
}
