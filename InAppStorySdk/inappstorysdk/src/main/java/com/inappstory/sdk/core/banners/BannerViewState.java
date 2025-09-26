package com.inappstory.sdk.core.banners;

public class BannerViewState {
    private Integer bannerId;
    private String bannerPlace;
    private String content;
    private int bannerPlaceIndex;
    private BannerLoadStates bannerLoadState = BannerLoadStates.EMPTY;
    private boolean bannerIsPaused = false;

    private boolean bannerIsActive = false;
    int contentStatus = 0;

    int slideJSStatus = 0;
    boolean renderReady;

    public boolean renderReady() {
        return renderReady;
    }

    public BannerViewState renderReady(boolean renderReady) {
        this.renderReady = renderReady;
        return this;
    }

    public int contentStatus() {
        return contentStatus;
    }

    public boolean bannerIsPaused() {
        return bannerIsPaused;
    }

    public int bannerPlaceIndex() {
        return bannerPlaceIndex;
    }

    public boolean bannerIsActive() {
        return bannerIsActive;
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


    public BannerViewState contentStatus(int contentStatus) {
        this.contentStatus = contentStatus;
        return this;
    }



    public BannerViewState bannerIsPaused(boolean bannerIsPaused) {
        this.bannerIsPaused = bannerIsPaused;
        return this;
    }



    public BannerViewState bannerIsActive(boolean bannerIsActive) {
        this.bannerIsActive = bannerIsActive;
        return this;
    }


    public BannerViewState slideJSStatus(int slideJSStatus) {
        this.slideJSStatus = slideJSStatus;
        return this;
    }

    public BannerViewState bannerId(Integer bannerId) {
        this.bannerId = bannerId;
        return this;
    }

    public BannerViewState bannerPlace(String bannerPlace) {
        this.bannerPlace = bannerPlace;
        return this;
    }

    public BannerViewState loadState(BannerLoadStates loadState) {
        this.bannerLoadState = loadState;
        return this;
    }

    public BannerViewState content(String content) {
        this.content = content;
        return this;
    }

    public BannerViewState() {
    }

    public BannerViewState copy() {
        return new BannerViewState()
                .content(this.content)
                .loadState(this.bannerLoadState)
                .contentStatus(this.contentStatus)
                .slideJSStatus(this.slideJSStatus)
                .renderReady(this.renderReady)
                .bannerPlace(this.bannerPlace)
                .bannerIsPaused(this.bannerIsPaused)
                .bannerIsActive(this.bannerIsActive)
                .bannerId(this.bannerId);
    }

    @Override
    public String toString() {
        return "BannerState{" +
                "bannerId=" + bannerId +
                ", bannerPlace='" + bannerPlace + '\'' +
                ", bannerLoadState=" + bannerLoadState +
                ", bannerIsPaused=" + bannerIsPaused +
                ", bannerIsActive=" + bannerIsActive +
                ", renderReady=" + renderReady +
                ", contentStatus=" + contentStatus +
                ", bannerPlaceIndex=" + bannerPlaceIndex +
                ", slideJSStatus=" + slideJSStatus +
                '}';
    }
}
