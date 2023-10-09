package com.inappstory.sdk.stories.outercallbacks.common.reader;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class SlideData implements Serializable {
    @NonNull
    public StoryData story;

    public int index;

    public String payload;

    public SlideData(
            @NonNull StoryData story,
            int index,
            String payload) {
        this.story = story;
        this.index = index;
        this.payload = payload;
    }
}
