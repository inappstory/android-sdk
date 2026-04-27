package com.inappstory.sdk.refactoring.stories.data.contracts;

import com.inappstory.sdk.refactoring.shared.data.contracts.IOpenedStatus;
import com.inappstory.sdk.refactoring.shared.data.contracts.IReaderCloseEnabler;
import com.inappstory.sdk.refactoring.shared.data.contracts.IReaderSwipeUpEnabler;
import com.inappstory.sdk.refactoring.shared.data.contracts.ISlidesContent;
import com.inappstory.sdk.refactoring.shared.data.contracts.IStatData;
import com.inappstory.sdk.refactoring.shared.data.contracts.IStatusContent;

public interface IStoryReaderItem extends
        IOpenedStatus,
        IReaderSwipeUpEnabler,
        IReaderCloseEnabler,
        ISlidesContent,
        IStatData,
        IStatusContent {
}
