package com.inappstory.sdk.refactoring.stories.data.contracts;

import com.inappstory.sdk.core.data.IContentWithTimeline;
import com.inappstory.sdk.refactoring.shared.data.contracts.IOpenedStatus;
import com.inappstory.sdk.refactoring.shared.data.contracts.IReaderCloseEnabler;
import com.inappstory.sdk.refactoring.shared.data.contracts.IReaderSwipeUpEnabler;
import com.inappstory.sdk.refactoring.shared.data.contracts.IStatData;
import com.inappstory.sdk.refactoring.shared.data.contracts.IStatusContent;

import java.util.Map;

public interface IStoryItem extends
        IStatData,
        IStatusContent,
        IOpenedStatus,
        IReaderSwipeUpEnabler,
        IReaderCloseEnabler,
        IContentWithTimeline {
    Map<String, Object> ugcPayload();
    boolean fullscreen();
}
