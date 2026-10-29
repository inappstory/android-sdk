package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.parse.ExtendedJsonObject;

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

            slide.slideIndex = jsonObj.optInt("slideIndex");
            slide.duration = jsonObj.optInt("duration");
            slide.html = jsonObj.optString("html");
            slide.isScreenshotShare = jsonObj.optBoolean("screenshot_share");

            slide.slidePayload = new ToSlidePayload(jsonObj.optJSONObject("event_payload")).parse();
            slide.slideTimeline = new ToStorySlideTimeline(jsonObj.optJSONObject("timeline")).parse();
            slide.resources = new ToContentResourceList(jsonObj.optJSONArray("resources")).parse();
            slide.placeholders = new ToContentResourceList(jsonObj.optJSONArray("img_placeholders_resources")).parse();
            return slide;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "StorySlide";
    }
}
