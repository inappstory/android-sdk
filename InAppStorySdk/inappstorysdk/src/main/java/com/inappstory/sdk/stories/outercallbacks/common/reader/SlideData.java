package com.inappstory.sdk.stories.outercallbacks.common.reader;

import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.api.models.ContentType;

import java.io.Serializable;

public class SlideData extends ContentData implements Serializable  {
    @NonNull
    /**
     * @deprecated Will be renamed and changed to private in next version
     * Use {@link #content()} instead.
     */
    @Deprecated
    public StoryData story;

    /**
     * @deprecated Will be changed to private in next version
     * Use {@link #index()} instead.
     */
    @Deprecated
    public int index;

    /**
     * @deprecated Will be changed to private in next version
     * Use {@link #payload()} instead.
     */
    @Deprecated
    public String payload;

    public StoryData content() {
        return story;
    }

    public int index() {
        return index;
    }

    public String payload() {
        return payload;
    }

    public SlideData(
            @NonNull StoryData story,
            int index,
            String payload) {
        super(story.sourceType(), ContentType.STORY);
        this.story = story;
        this.index = index;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "SlideData {" +
                "content=" + content() +
                ", index='" + index() + '\'' +
                ", payload='" + payload() + '\'' +
                '}';
    }
}
