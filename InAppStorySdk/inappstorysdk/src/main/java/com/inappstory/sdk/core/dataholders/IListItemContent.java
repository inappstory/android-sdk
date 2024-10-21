package com.inappstory.sdk.core.dataholders;

public interface IListItemContent extends IStatData {
    String title();
    String backgroundColor();
    String imageCoverByQuality(int quality);
    String videoCover();
    boolean isOpened();
    void setOpened(boolean isOpened);
    boolean hasAudio();

    String deeplink();
    String gameInstanceId();

    boolean hideInReader();
}
