package com.inappstory.sdk.core.banners;


import com.inappstory.sdk.core.data.IBanner;

import java.util.ArrayList;
import java.util.List;

public class BannerPagerState {
    public BannerPlaceAppearance getAppearance() {
        return appearance;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public BannerPagerLoadStates loadState() {
        return loadState;
    }

    public List<IBanner> getItems() {
        return items;
    }

    public String place() {
        return place;
    }


    public BannerPagerState appearance(BannerPlaceAppearance appearance) {
        this.appearance = appearance;
        return this;
    }

    public BannerPagerState currentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        return this;
    }

    public BannerPagerState loadState(BannerPagerLoadStates loadState) {
        this.loadState = loadState;
        return this;
    }

    public BannerPagerState items(List<IBanner> items) {
        this.items = items;
        return this;
    }

    public BannerPagerState place(String place) {
        this.place = place;
        return this;
    }

    BannerPlaceAppearance appearance;
    int currentIndex = 0;
    String place = "";
    BannerPagerLoadStates loadState;
    List<IBanner> items = new ArrayList<>();


    public BannerPagerState copy() {
        return new BannerPagerState()
                .items(this.items)
                .currentIndex(this.currentIndex)
                .appearance(this.appearance)
                .place(this.place)
                .loadState(this.loadState);
    }
}
