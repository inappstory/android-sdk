package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.stories.api.models.ContentType;

public class BannerData extends ContentData {
    private int id;

    private String bannerPlace;

    public BannerData(
            int id,
            String bannerPlace
    ) {
        super(SourceType.BANNERS, ContentType.BANNER);
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
                '}';
    }
}
