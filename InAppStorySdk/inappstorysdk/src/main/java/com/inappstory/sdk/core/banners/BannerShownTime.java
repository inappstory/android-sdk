package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.data.IShownTime;

public class BannerShownTime implements IShownTime {
    @Override
    public int id() {
        return bannerId;
    }

    @Override
    public long latestShownTime() {
        return latestShownTime;
    }

    int bannerId;
    long latestShownTime = 0;

    @Override
    public void updateLatestShownTime() {
        this.latestShownTime = System.currentTimeMillis();
    }

    public BannerShownTime(int bannerId) {
        this.bannerId = bannerId;
        this.latestShownTime = System.currentTimeMillis();
    }

    public BannerShownTime(String idAndTime) {
        if (idAndTime == null) return;
        String[] idTime = idAndTime.split("_");
        bannerId = Integer.parseInt(idTime[0]);
        latestShownTime = Long.parseLong(idTime[1]);
    }

    @Override
    public String getSaveKey() {
        return bannerId + "_" + latestShownTime;
    }
}
