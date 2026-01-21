package com.inappstory.sdk.core.data;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;

public interface IInAppMessage extends IReaderContent {
    int id();
    boolean hasPlaceholders();
    boolean hasLimit();
    long frequencyLimit();
    long displayFrom();
    long displayTo();
    InAppMessageAppearance inAppMessageAppearance();
    int getEventPriority(String eventToCompare);
    boolean belongsToEvent(String eventToCompare);
}
