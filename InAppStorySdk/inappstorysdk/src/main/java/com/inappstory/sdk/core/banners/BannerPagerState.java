package com.inappstory.sdk.core.banners;


public class BannerPagerState {
    public BannerPlaceAppearance getAppearance() {
        return appearance;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean isLoading() {
        return loading;
    }

    public int[] getItemsIds() {
        return itemsIds;
    }

    public String place() {
        return place;
    }

    public boolean loaded() {
        return loaded;
    }

    public BannerPagerState appearance(BannerPlaceAppearance appearance) {
        this.appearance = appearance;
        return this;
    }

    public BannerPagerState currentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        return this;
    }

    public BannerPagerState isLoading(boolean loading) {
        this.loading = loading;
        return this;
    }

    public BannerPagerState loaded(boolean loaded) {
        this.loaded = loaded;
        return this;
    }

    public BannerPagerState itemsIds(int[] itemsIds) {
        this.itemsIds = itemsIds;
        return this;
    }

    public BannerPagerState place(String place) {
        this.place = place;
        return this;
    }

    BannerPlaceAppearance appearance;
    int currentIndex = 0;
    String place = "";
    boolean loading = false;
    boolean loaded = false;
    int[] itemsIds = new int[0];


    public BannerPagerState copy() {
        return new BannerPagerState()
                .itemsIds(this.itemsIds)
                .currentIndex(this.currentIndex)
                .appearance(this.appearance)
                .place(this.place)
                .loaded(this.loaded)
                .isLoading(this.loading);
    }
}
