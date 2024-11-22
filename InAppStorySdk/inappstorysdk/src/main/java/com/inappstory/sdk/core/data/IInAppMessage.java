package com.inappstory.sdk.core.data;

import com.inappstory.sdk.inappmessage.IAMUiContainerType;

public interface IInAppMessage extends IReaderContent {
    int id();
    boolean hasPlaceholders();
    int frequencyLimit();
    IAMUiContainerType screenType();
}
