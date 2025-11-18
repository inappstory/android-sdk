package com.inappstory.sdk.banners;

import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ContentData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;

public class BannerData extends ContentData {
    private int id;

    private String bannerPlace;

    private String payload;

    public String payload() {
        return payload;
    }

    public BannerData(
            int id,
            String bannerPlace
    ) {
        super(SourceType.BANNERS, ContentType.BANNER);
        this.id = id;
        this.bannerPlace = bannerPlace;
    }


    public BannerData(
            int id,
            String bannerPlace,
            String payload
    ) {
        super(SourceType.BANNERS, ContentType.BANNER);
        this.payload = payload;
        this.id = id;
        this.bannerPlace = bannerPlace;
    }

    public int id() {
        return id;
    }

    public String bannerPlace() {
        return bannerPlace;
    }

    @Override
    public String toString() {
        return "BannerData{" +
                "id=" + id() +
                ", title='" + bannerPlace() + '\'' +
                ", payload='" + payload() + '\'' +
                '}';
    }
}
