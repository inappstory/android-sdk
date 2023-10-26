package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.HashMap;

public class UgcStoryData extends StoryData {
    public HashMap<String, Object> ugcPayload;

    public UgcStoryData(
            int id,
            String title,
            String tags,
            int slidesCount,
            HashMap<String, Object> ugcPayload,
            SourceType sourceType
    ) {
        super(id, Story.StoryType.UGC, title, tags, slidesCount, null, sourceType);
        this.ugcPayload = ugcPayload;
    }

    public UgcStoryData(
            Story story,
            SourceType sourceType
    ) {
        this(
                story.id,
                StringsUtils.getNonNull(story.statTitle),
                StringsUtils.getNonNull(story.tags),
                story.slidesCount,
                story.ugcPayload,
                sourceType
        );
    }

    @Override
    public String toString() {
        return "UgcStoryData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tags='" + tags + '\'' +
                ", sourceType='" + sourceType.name() + '\'' +
                ", slidesCount=" + slidesCount +
                ", ugcPayload=" + ugcPayload +
                '}';
    }
}
