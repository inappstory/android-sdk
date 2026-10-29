package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.core.network.content.models.StorySlideTimeline;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.SlidePayload;

import org.json.JSONObject;


public final class ToSlidePayload extends ToModelParser<SlidePayload> {
    public ToSlidePayload(JSONObject value) {
        super(value);
    }

    @Override
    public SlidePayload parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            SlidePayload slidePayload = new SlidePayload();
            slidePayload.eventType = jsonObj.optStringNullable("event_type");
            slidePayload.slideIndex = jsonObj.optInt("slide_index");
            slidePayload.payload = jsonObj.optStringNullable("payload");
            return slidePayload;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "SlidePayload";
    }
}
