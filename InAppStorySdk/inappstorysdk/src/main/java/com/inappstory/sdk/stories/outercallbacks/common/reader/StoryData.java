package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.core.data.IStatData;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.HashMap;
import java.util.Map;

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

    private final Map<String, String> extraFields = new HashMap<>();

    public StoryData(IStatData story, String feed, SourceType sourceType) {
        this(
                story.id(),
                ContentType.STORY,
                StringsUtils.getNonNull(story.statTitle()),
                story.slidesCount(),
                feed,
                sourceType,
                story.customAttributes()
        );
    }

    protected StoryData(
            int id,
            ContentType contentType,
            String title,
            int slidesCount,
            String feed,
            SourceType sourceType,
            Map<String, String> extraFields
    ) {
        super(sourceType, contentType);
        this.id = id;
        this.title = title;
        this.slidesCount = slidesCount;
        if (extraFields != null) {
            this.extraFields.putAll(extraFields);
        }
        this.feed = feed;
    }


    public int id() {
        return id;
    }

    public String title() {
        return title;
    }

    public Map<String, String> extraFields() {
        return new HashMap<>(extraFields);
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
                ", extraFields =" + extraFields() +
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
