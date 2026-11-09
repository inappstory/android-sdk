package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;


public final class ToStorySlide extends ToModelParser<StorySlide> {
    public ToStorySlide(JSONObject value) {
        super(value);
    }

    @Override
    public StorySlide parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            StorySlide slide = new StorySlide();

            slide.slideIndex = jsonObj.optInt(
                    ModelFields.StorySlide.slideIndex
            );
            slide.duration = jsonObj.optInt(
                    ModelFields.StorySlide.duration
            );
            slide.html = jsonObj.optString(
                    ModelFields.StorySlide.html
            );
            slide.isScreenshotShare = jsonObj.optBoolean(
                    ModelFields.StorySlide.isScreenshotShare
            );

            slide.slidePayload = new ToSlidePayload(jsonObj.optJSONObject(
                    ModelFields.StorySlide.slidePayload
            )).parse();
            slide.slideTimeline = new ToStorySlideTimeline(jsonObj.optJSONObject(
                    ModelFields.StorySlide.slideTimeline
            )).parse();
            slide.resources = new ToContentResourceList(jsonObj.optJSONArray(
                    ModelFields.StorySlide.resources
            )).parse();
            slide.placeholders = new ToContentResourceList(jsonObj.optJSONArray(
                    ModelFields.StorySlide.placeholders
            )).parse();
            return slide;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "StorySlide";
    }
}
