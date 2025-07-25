package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.core.data.IStatData;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.Map;

public class UgcStoryData extends StoryData {
    public Map<String, Object> ugcPayload;

    private UgcStoryData(
            int id,
            String title,
            int slidesCount,
            Map<String, Object> ugcPayload,
            SourceType sourceType
    ) {
        super(id, ContentType.UGC, title, slidesCount, null, sourceType);
        this.ugcPayload = ugcPayload;
    }

    public UgcStoryData(
            IStatData story,
            SourceType sourceType
    ) {
        this(
                story.id(),
                StringsUtils.getNonNull(story.statTitle()),
                story.slidesCount(),
                story.ugcPayload(),
                sourceType
        );
    }

    @Override
    public String toString() {
        return "UgcStoryData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", sourceType='" + sourceType().name() + '\'' +
                ", slidesCount=" + slidesCount +
                ", ugcPayload=" + ugcPayload +
                '}';
    }
}
