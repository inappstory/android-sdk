package com.inappstory.sdk.core.ui.screens.inappmessagereader;

import com.inappstory.sdk.core.data.IShownTime;

public class IAMShownTime implements IShownTime {
    int iamId;
    long latestShownTime = 0;

    @Override
    public void updateLatestShownTime() {
        this.latestShownTime = System.currentTimeMillis();
    }

    public IAMShownTime(int iamId) {
        this.iamId = iamId;
        this.latestShownTime = System.currentTimeMillis();
    }

    public IAMShownTime(String idAndTime) {
        if (idAndTime == null) return;
        String[] idTime = idAndTime.split("_");
        iamId = Integer.parseInt(idTime[0]);
        latestShownTime = Long.parseLong(idTime[1]);
    }

    @Override
    public int id() {
        return iamId;
    }

    @Override
    public long latestShownTime() {
        return latestShownTime;
    }

    @Override
    public String getSaveKey() {
        return iamId + "_" + latestShownTime;
    }
}
