package com.inappstory.sdk.core.banners;

public class BannerContentState {
    private Integer bannerId;
    private String bannerPlace;
    private String content;
    private BannerLoadStates bannerLoadState = BannerLoadStates.EMPTY;
    private int contentStatus = 0;


    public BannerContentState contentStatus(int contentStatus) {
        this.contentStatus = contentStatus;
        return this;
    }

    public int contentStatus() {
        return contentStatus;
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


    public BannerContentState bannerId(Integer bannerId) {
        this.bannerId = bannerId;
        return this;
    }

    public BannerContentState bannerPlace(String bannerPlace) {
        this.bannerPlace = bannerPlace;
        return this;
    }

    public BannerContentState loadState(BannerLoadStates loadState) {
        this.bannerLoadState = loadState;
        return this;
    }

    public BannerContentState content(String content) {
        this.content = content;
        return this;
    }

    public BannerContentState() {
    }

    public BannerContentState copy() {
        return new BannerContentState()
                .content(this.content)
                .loadState(this.bannerLoadState)
                .contentStatus(this.contentStatus)
                .bannerPlace(this.bannerPlace)
                .bannerId(this.bannerId);
    }

    @Override
    public String toString() {
        return "BannerState{" +
                "bannerId=" + bannerId +
                ", bannerPlace='" + bannerPlace + '\'' +
                ", bannerLoadState=" + bannerLoadState +
                '}';
    }
}
