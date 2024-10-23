package com.inappstory.sdk.core.dataholders;

public interface IListItemContent extends IStatData, IContentWithStatus {
    String title();
    String titleColor();
    String backgroundColor();
    String imageCoverByQuality(int quality);
    String videoCover();
    boolean hasAudio();
    boolean hasSwipeUp();
    boolean disableClose();
    String deeplink();
    String gameInstanceId();

    boolean hideInReader();


}
