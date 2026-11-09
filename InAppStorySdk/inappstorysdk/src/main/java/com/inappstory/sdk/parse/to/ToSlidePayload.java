package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.core.network.content.models.StorySlideTimeline;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
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
            slidePayload.eventType = jsonObj.optStringNullable(
                    ModelFields.SlidePayload.eventType
            );
            slidePayload.slideIndex = jsonObj.optInt(
                    ModelFields.SlidePayload.slideIndex
            );
            slidePayload.payload = jsonObj.optStringNullable(
                    ModelFields.SlidePayload.payload
            );
            return slidePayload;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "SlidePayload";
    }
}
