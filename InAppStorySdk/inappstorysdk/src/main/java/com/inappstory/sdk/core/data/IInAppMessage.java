package com.inappstory.sdk.core.data;

public interface IInAppMessage extends IReaderContent {
    int id();
    boolean hasPlaceholders();
    int frequencyLimit();
    int screenType();
}
