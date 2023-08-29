package com.inappstory.sdk.stories.outercallbacks.common.reader;

import androidx.annotation.NonNull;

public class SlideData {
    @NonNull
    public StoryData story;

    public int index;

    public SlideData() {}
    public SlideData(@NonNull StoryData story, int index) {
        this.story = story;
        this.index = index;
    }
}
