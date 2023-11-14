package com.inappstory.sdk.stories.outercallbacks.common.objects;

import com.inappstory.sdk.core.utils.network.annotations.models.Ignore;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.models.api.Story;
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
    public Story.StoryType storyType;

    public StoryData() {
    }

    public StoryData(Story story, String feed, SourceType sourceType) {
        this(
                story.id,
                Story.StoryType.COMMON,
                StringsUtils.getNonNull(story.statTitle),
                StringsUtils.getNonNull(story.tags),
                story.slidesCount,
                feed,
                sourceType
        );
    }

    public StoryData(IStoryDTO story, String feed, SourceType sourceType) {
        this(
                story.getId(),
                Story.StoryType.COMMON,
                StringsUtils.getNonNull(story.getStatTitle()),
                StringsUtils.getNonNull(story.getTags()),
                story.getSlidesCount(),
                feed,
                sourceType
        );
    }

    public StoryData(IPreviewStoryDTO story, String feed, SourceType sourceType) {
        this(
                story.getId(),
                Story.StoryType.COMMON,
                StringsUtils.getNonNull(story.getStatTitle()),
                StringsUtils.getNonNull(story.getTags()),
                story.getSlidesCount(),
                feed,
                sourceType
        );
    }

    public StoryData(
            int id,
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
        this.storyType = Story.StoryType.COMMON;
        this.feed = feed;
        this.sourceType = sourceType;
    }

    public StoryData(
            int id,
            Story.StoryType storyType,
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
        this.storyType = storyType;
        this.feed = feed;
        this.sourceType = sourceType;
    }

    @Override
    public String toString() {
        return "StoryData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tags='" + tags + '\'' +
                ", slidesCount=" + slidesCount +
                '}';
    }
}
