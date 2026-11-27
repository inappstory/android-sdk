package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.inappmessage.domain.stedata.STEData;

import java.util.List;

public class UpdateBannerItemsData implements STEData {
    private final List<IBanner> items;
    private final int id;
    private final UpdateBannerItemsType type;


    public UpdateBannerItemsData(
            List<IBanner> items,
            int id,
            UpdateBannerItemsType type
    ) {
        this.items = items;
        this.id = id;
        this.type = type;
    }

    public List<IBanner> items() {
        return items;
    }

    public int id() {
        return id;
    }

    public UpdateBannerItemsType type() {
        return type;
    }
}
