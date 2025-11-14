package com.inappstory.sdk.core.banners;


import android.util.Log;

import com.inappstory.sdk.core.data.IBanner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BannerPlaceState implements IBannerPlaceState {

    public Integer currentIndex() {
        return currentIndex;
    }

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

    public BannerPlaceState() {
        Log.e("BannerPlaceState", "Create");
    }

    private BannerPlaceState(List<IBanner> items) {
        Log.e("BannerPlaceState", "Copy");
        this.items = new ArrayList<>(items);
    }

    public BannerPlaceState currentIndex(Integer currentIndex) {
        Log.e("BannerPlaceState", "currentIndex " + currentIndex);
        this.currentIndex = currentIndex;
        return this;
    }

    public BannerPlaceState iterationId(String iterationId) {
        this.iterationId = iterationId;
        return this;
    }

    public BannerPlaceState loadState(BannersWidgetLoadStates loadState) {
        this.loadState = loadState;
        return this;
    }

    public BannerPlaceState items(List<IBanner> items) {
        this.items = items;
        return this;
    }

    public BannerPlaceState tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public BannerPlaceState placeId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    BannerCarouselAppearance appearance;
    Integer currentIndex;
    String placeId = "";
    String iterationId = UUID.randomUUID().toString();
    BannersWidgetLoadStates loadState = BannersWidgetLoadStates.NONE;
    List<IBanner> items = new ArrayList<>();
    List<String> tags = new ArrayList<>();

    public BannerPlaceState copy() {
        return new BannerPlaceState(this.items)
                .currentIndex(this.currentIndex)
                .iterationId(this.iterationId)
                .tags(this.tags)
                .placeId(this.placeId)
                .loadState(this.loadState);
    }
}
