package com.inappstory.sdk.core.data;

import com.inappstory.sdk.inappmessage.IAMUiContainerType;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;

public interface IInAppMessage extends IReaderContent {
    int id();
    boolean hasPlaceholders();
    boolean hasLimit();
    long frequencyLimit();
    long displayFrom();
    long displayTo();
    IAMUiContainerType screenType();
    InAppMessageAppearance inAppMessageAppearance();
    int getEventPriority(String eventToCompare);
    boolean belongsToEvent(String eventToCompare);
}
