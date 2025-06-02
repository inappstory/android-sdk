package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.stories.api.models.ContentType;

public class BannerData extends ContentData {
    private int id;

    private String title;

    public BannerData(
            int id,
            String campaignName,
            SourceType sourceType
    ) {
        super(sourceType, ContentType.BANNER);
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
        return "BannerData{" +
                "id=" + id() +
                ", title='" + title() + '\'' +
                '}';
    }
}
