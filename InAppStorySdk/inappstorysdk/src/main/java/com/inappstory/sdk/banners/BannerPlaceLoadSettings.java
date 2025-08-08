package com.inappstory.sdk.banners;

import java.util.ArrayList;
import java.util.List;

public class BannerPlaceLoadSettings {
    private String placeId;

    private List<String> tags;

    public String placeId() {
        return placeId;
    }

    public List<String> tags() {
        return tags;
    }


    public BannerPlaceLoadSettings placeId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    public BannerPlaceLoadSettings tags(List<String> tags) {
        if (tags != null)
            this.tags = new ArrayList<>(tags);
        return this;
    }
}
