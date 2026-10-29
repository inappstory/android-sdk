package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.core.network.content.models.StorySlideTimeline;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToStorySlideTimeline extends ToModelParser<StorySlideTimeline> {
    public ToStorySlideTimeline(JSONObject value) {
        super(value);
    }

    @Override
    public StorySlideTimeline parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            StorySlideTimeline slideTimeline = new StorySlideTimeline();
            slideTimeline.foregroundColor = jsonObj.optStringNullable("foreground");
            slideTimeline.backgroundColor = jsonObj.optStringNullable("background");
            slideTimeline.isTimelineHidden = jsonObj.optBoolean("is_timeline_hidden");
            return slideTimeline;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "StorySlideTimeline";
    }
}
