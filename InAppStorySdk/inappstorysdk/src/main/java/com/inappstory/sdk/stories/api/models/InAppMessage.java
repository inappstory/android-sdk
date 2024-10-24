package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.core.dataholders.IReaderContent;

public interface InAppMessage extends IReaderContent {
    int id();
    String campaignName();
    boolean hasPlaceholder();
    int dayLimit();
}
