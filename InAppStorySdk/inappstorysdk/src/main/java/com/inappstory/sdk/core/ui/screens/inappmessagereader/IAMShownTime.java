package com.inappstory.sdk.core.ui.screens.inappmessagereader;

public class IAMShownTime {
    int iamId;
    long latestShownTime = 0;

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

    public String getSaveKey() {
        return iamId + "_" + latestShownTime;
    }
}
