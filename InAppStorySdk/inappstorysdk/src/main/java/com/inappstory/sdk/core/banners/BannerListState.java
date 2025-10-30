package com.inappstory.sdk.core.banners;


import android.util.Log;

import com.inappstory.sdk.core.data.IBanner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BannerListState implements IBannerWidgetState {

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

    public BannerListState() {
        Log.e("BannerPlaceState", "Create");
    }

    private BannerListState(List<IBanner> items) {
        Log.e("BannerPlaceState", "Copy");
        this.items = new ArrayList<>(items);
    }


    public BannerListState iterationId(String iterationId) {
        this.iterationId = iterationId;
        return this;
    }

    public BannerListState loadState(BannersWidgetLoadStates loadState) {
        this.loadState = loadState;
        return this;
    }

    public BannerListState items(List<IBanner> items) {
        this.items = items;
        return this;
    }

    public BannerListState tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public BannerListState placeId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    String placeId = "";
    String iterationId = UUID.randomUUID().toString();
    BannersWidgetLoadStates loadState = BannersWidgetLoadStates.NONE;
    List<IBanner> items = new ArrayList<>();
    List<String> tags = new ArrayList<>();

    public BannerListState copy() {
        return new BannerListState(this.items)
                .iterationId(this.iterationId)
                .tags(this.tags)
                .placeId(this.placeId)
                .loadState(this.loadState);
    }
}
