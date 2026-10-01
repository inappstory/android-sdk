package com.inappstory.sdk.core.banners;


import com.inappstory.sdk.core.data.IBanner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BannerGridState implements IBannerWidgetState {

    public List<String> tags() {
        return tags;
    }

    public String iterationId() {
        return iterationId;
    }

    public BannersWidgetLoadStates loadState() {
        return loadState;
    }

    public List<IBanner> getItems() {
        return items;
    }

    public String placeId() {
        return placeId;
    }

    public BannerGridState() {
    }

    private BannerGridState(List<IBanner> items) {
        this.items = new ArrayList<>(items);
    }


    public BannerGridState iterationId(String iterationId) {
        this.iterationId = iterationId;
        return this;
    }

    public BannerGridState loadState(BannersWidgetLoadStates loadState) {
        this.loadState = loadState;
        return this;
    }

    public BannerGridState items(List<IBanner> items) {
        this.items = items;
        return this;
    }

    public BannerGridState tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public BannerGridState placeId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    String placeId = "";
    String iterationId = UUID.randomUUID().toString();
    BannersWidgetLoadStates loadState = BannersWidgetLoadStates.NONE;
    List<IBanner> items = new ArrayList<>();
    List<String> tags = new ArrayList<>();

    public BannerGridState copy() {
        return new BannerGridState(this.items)
                .iterationId(this.iterationId)
                .tags(this.tags)
                .placeId(this.placeId)
                .loadState(this.loadState);
    }
}
