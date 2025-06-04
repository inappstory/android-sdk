package com.inappstory.sdk.core.banners;

public class BannerState {
    private Integer bannerId;
    private String bannerPlace;
    private String content;
    private BannerLoadStates bannerLoadState = BannerLoadStates.EMPTY;
    int contentStatus = 0;

    int slideJSStatus = 0;

    public int contentStatus() {
        return contentStatus;
    }

    public int slideJSStatus() {
        return slideJSStatus;
    }

    public Integer bannerId() {
        return bannerId;
    }

    public String bannerPlace() {
        return bannerPlace;
    }

    public String content() {
        return content;
    }

    public BannerLoadStates loadState() {
        return bannerLoadState;
    }


    public BannerState contentStatus(int contentStatus) {
        this.contentStatus = contentStatus;
        return this;
    }


    public BannerState slideJSStatus(int slideJSStatus) {
        this.slideJSStatus = slideJSStatus;
        return this;
    }

    public BannerState bannerId(Integer bannerId) {
        this.bannerId = bannerId;
        return this;
    }

    public BannerState bannerPlace(String bannerPlace) {
        this.bannerPlace = bannerPlace;
        return this;
    }

    public BannerState loadState(BannerLoadStates loadState) {
        this.bannerLoadState = loadState;
        return this;
    }

    public BannerState content(String content) {
        this.content = content;
        return this;
    }

    public BannerState() {
    }

    public BannerState copy() {
        return new BannerState()
                .content(this.content)
                .loadState(this.bannerLoadState)
                .contentStatus(this.contentStatus)
                .slideJSStatus(this.slideJSStatus)
                .bannerPlace(this.bannerPlace)
                .bannerId(this.bannerId);
    }
}
