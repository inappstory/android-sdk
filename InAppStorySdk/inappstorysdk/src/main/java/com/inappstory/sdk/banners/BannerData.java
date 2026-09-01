package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ContentData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;

import java.util.HashMap;
import java.util.Map;

public class BannerData extends ContentData {
    private int id;

    private String bannerPlace;

    private String payload;

    public String payload() {
        return payload;
    }

    private final Map<String, String> extraFields = new HashMap<>();

    public Map<String, String> extraFields() {
        return extraFields;
    }

    public BannerData(
            int id,
            String bannerPlace,
            String payload
    ) {
        super(SourceType.BANNERS, ContentType.BANNER);
        this.id = id;
        this.bannerPlace = bannerPlace;
    }

    public BannerData(IBanner banner, String bannerPlace) {
        super(SourceType.BANNERS, ContentType.BANNER);
        this.payload = banner.slideEventPayload(0);
        this.id = banner.id();
        if (banner.customAttributes() != null) this.extraFields.putAll(banner.customAttributes());
        this.bannerPlace = bannerPlace;
    }

    public BannerData(
            int id,
            String bannerPlace,
            String payload,
            Map<String, String> extraFields
    ) {
        super(SourceType.BANNERS, ContentType.BANNER);
        this.payload = payload;
        this.id = id;
        if (extraFields != null) this.extraFields.putAll(extraFields);
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
                ", extraFields =" + extraFields() +
                '}';
    }
}
