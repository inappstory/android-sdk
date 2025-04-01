package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.core.data.IStatData;
import com.inappstory.sdk.network.annotations.models.Ignore;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.Serializable;

public class StoryData extends ContentData {
    /**
     * @deprecated Will be changed to private in next version
     * Use {@link #id()} instead.
     */
    @Deprecated
    public int id;

    /**
     *
     * @deprecated Will be changed to private in next version
     * Use {@link #title()} instead.
     */
    @Deprecated
    public String title;

    /**
     * @deprecated Will be changed to private in next version
     * Use {@link #feed()} instead.
     */
    @Deprecated
    public String feed;

    /**
     * @deprecated Will be changed to private in next version
     * Use {@link #slidesCount()} instead.
     */
    @Deprecated
    public int slidesCount;

    public StoryData(IStatData story, String feed, SourceType sourceType) {
        this(
                story.id(),
                ContentType.STORY,
                StringsUtils.getNonNull(story.statTitle()),
                story.slidesCount(),
                feed,
                sourceType
        );
    }

    protected StoryData(
            int id,
            ContentType contentType,
            String title,
            int slidesCount,
            String feed,
            SourceType sourceType
    ) {
        super(sourceType, contentType);
        this.id = id;
        this.title = title;
        this.slidesCount = slidesCount;
        this.feed = feed;
    }


    public int id() {
        return id;
    }

    public String title() {
        return title;
    }


    public String feed() {
        return feed;
    }

    public int slidesCount() {
        return slidesCount;
    }

    @Override
    public String toString() {
        return "StoryData{" +
                "id=" + id() +
                ", title='" + title() + '\'' +
                ", feed='" + feed() + '\'' +
                ", sourceType='" + sourceType().name() + '\'' +
                ", contentType='" + contentType().name() + '\'' +
                ", slidesCount=" + slidesCount() +
                '}';
    }

    public static StoryData getStoryData(
            IStatData story,
            String feed,
            SourceType sourceType,
            ContentType contentType
    ) {
        if (contentType == ContentType.STORY) {
            return new StoryData(story, feed, sourceType);
        } else {
            return new UgcStoryData(story, sourceType);
        }
    }
}
