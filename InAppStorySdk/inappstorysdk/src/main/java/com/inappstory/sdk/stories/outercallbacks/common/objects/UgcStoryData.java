package com.inappstory.sdk.stories.outercallbacks.common.objects;

import com.inappstory.sdk.core.models.api.Story;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.HashMap;

public class UgcStoryData extends StoryData {
    public HashMap<String, Object> payload;

    public UgcStoryData(
            Story story,
            SourceType sourceType
    ) {
        super(
                story.id,
                Story.StoryType.COMMON,
                StringsUtils.getNonNull(story.statTitle),
                StringsUtils.getNonNull(story.tags),
                story.slidesCount,
                null,
                sourceType
        );
        this.payload = story.payload;
    }
}
