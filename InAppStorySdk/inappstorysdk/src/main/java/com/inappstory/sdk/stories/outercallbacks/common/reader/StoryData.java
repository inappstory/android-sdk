package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.network.annotations.models.Ignore;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.Serializable;

public class StoryData implements Serializable {
    public int id;
    public String title;
    public String tags;
    public String feed;
    public SourceType sourceType;
    public int slidesCount;
    @Ignore
    public ContentType contentType;

    public StoryData(
            int id,
            String title,
            String tags,
            int slidesCount,
            String feed,
            SourceType sourceType
    ) {
        this(id, ContentType.COMMON, title, tags, slidesCount, feed, sourceType);
    }

    public StoryData(Story story, String feed, SourceType sourceType) {
        this(
                story.id,
                ContentType.COMMON,
                StringsUtils.getNonNull(story.statTitle),
                StringsUtils.getNonNull(story.tags),
                story.slidesCount,
                feed,
                sourceType
        );
    }

    protected StoryData(
            int id,
            ContentType contentType,
            String title,
            String tags,
            int slidesCount,
            String feed,
            SourceType sourceType
    ) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.slidesCount = slidesCount;
        this.contentType = contentType;
        this.feed = feed;
        this.sourceType = sourceType;
    }

    @Override
    public String toString() {
        return "StoryData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tags='" + tags + '\'' +
                ", feed='" + feed + '\'' +
                ", sourceType='" + sourceType.name() + '\'' +
                ", slidesCount=" + slidesCount +
                '}';
    }

    public static StoryData getStoryData(
            Story story,
            String feed,
            SourceType sourceType,
            ContentType contentType
    ) {
        if (contentType == ContentType.COMMON) {
            return new StoryData(story, feed, sourceType);
        } else {
            return new UgcStoryData(story, sourceType);
        }
    }
}
