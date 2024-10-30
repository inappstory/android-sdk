package com.inappstory.sdk.core.dataholders.models;

public interface IInAppMessage extends IReaderContent {
    int id();
    boolean hasPlaceholders();
    int dayLimit();
}
