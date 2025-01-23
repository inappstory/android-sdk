package com.inappstory.sdk.core.data;

import com.inappstory.sdk.inappmessage.IAMUiContainerType;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;

public interface IInAppMessage extends IReaderContent {
    int id();
    boolean hasPlaceholders();
    long frequencyLimit();
    long displayFrom();
    long displayTo();
    IAMUiContainerType screenType();
    InAppMessageAppearance inAppMessageAppearance();
    boolean belongsToEvent(String eventToCompare);
}
