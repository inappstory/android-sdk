package com.inappstory.sdk.core.data;

public interface IShownTime {
    int id();
    long latestShownTime();
    String getSaveKey();
    void updateLatestShownTime();
}
