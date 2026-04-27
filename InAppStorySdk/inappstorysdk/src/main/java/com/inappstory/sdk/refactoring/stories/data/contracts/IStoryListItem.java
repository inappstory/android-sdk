package com.inappstory.sdk.refactoring.stories.data.contracts;

import com.inappstory.sdk.core.data.IStatData;
import com.inappstory.sdk.refactoring.shared.data.contracts.IOpenedStatus;
import com.inappstory.sdk.refactoring.shared.data.contracts.IStatusContent;

public interface IStoryListItem extends IStatData, IOpenedStatus, IStatusContent {
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
