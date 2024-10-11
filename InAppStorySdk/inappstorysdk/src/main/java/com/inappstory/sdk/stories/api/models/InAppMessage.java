package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.stories.api.interfaces.Copyable;
import com.inappstory.sdk.stories.api.interfaces.SlidesContentHolder;

public interface InAppMessage extends Copyable<InAppMessage>, SlidesContentHolder {
    int id();
    String campaignName();
    boolean hasPlaceholder();
    int dayLimit();
}
