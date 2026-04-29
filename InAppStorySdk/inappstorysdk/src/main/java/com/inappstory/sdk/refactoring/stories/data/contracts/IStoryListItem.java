package com.inappstory.sdk.refactoring.stories.data.contracts;

public interface IStoryListItem extends IStoryItem {
    String title();
    String titleColor();
    String backgroundColor();
    String imageCoverByQuality(int quality);
    String videoCover();
    boolean hasAudio();
    String deeplink();
    String gameInstanceId();
    boolean hideInReader();
}
