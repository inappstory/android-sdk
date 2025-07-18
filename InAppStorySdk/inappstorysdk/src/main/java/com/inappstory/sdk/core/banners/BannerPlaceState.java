package com.inappstory.sdk.core.banners;


import android.util.Log;

import com.inappstory.sdk.core.data.IBanner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BannerPlaceState {
    public BannerPlaceAppearance getAppearance() {
        return appearance;
    }

    public Integer currentIndex() {
        return currentIndex;
    }


    public String iterationId() {
        return iterationId;
    }

    public BannerPlaceLoadStates loadState() {
        return loadState;
    }

    public List<IBanner> getItems() {
        return items;
    }

    public String place() {
        return place;
    }

    public BannerPlaceState() {
        Log.e("BannerPlaceState", "Create");
    }

    private BannerPlaceState(List<IBanner> items) {
        Log.e("BannerPlaceState", "Copy");
        this.items = new ArrayList<>(items);
    }

    public BannerPlaceState appearance(BannerPlaceAppearance appearance) {
        this.appearance = appearance;
        return this;
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

    public BannerPlaceState loadState(BannerPlaceLoadStates loadState) {
        this.loadState = loadState;
        return this;
    }

    public BannerPlaceState items(List<IBanner> items) {
        this.items = items;
        return this;
    }

    public BannerPlaceState place(String place) {
        this.place = place;
        return this;
    }

    BannerPlaceAppearance appearance;
    Integer currentIndex;
    String place = "";
    String iterationId = UUID.randomUUID().toString();
    BannerPlaceLoadStates loadState = BannerPlaceLoadStates.NONE;
    List<IBanner> items = new ArrayList<>();


    public BannerPlaceState copy() {
        return new BannerPlaceState(this.items)
                .currentIndex(this.currentIndex)
                .iterationId(this.iterationId)
                .appearance(this.appearance)
                .place(this.place)
                .loadState(this.loadState);
    }
}
