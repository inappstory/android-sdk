package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.BannerSlide;
import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToBannerSlide extends ToModelParser<BannerSlide> {
    public ToBannerSlide(JSONObject value) {
        super(value);
    }

    @Override
    public BannerSlide parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            BannerSlide slide = new BannerSlide();

            slide.slideIndex = jsonObj.optInt("slideIndex");
            slide.duration = jsonObj.optInt("duration");
            slide.html = jsonObj.optString("html");

            slide.slidePayload = new ToSlidePayload(jsonObj.optJSONObject("event_payload")).parse();
            slide.resources = new ToContentResourceList(jsonObj.optJSONArray("resources")).parse();
            slide.placeholders = new ToContentResourceList(jsonObj.optJSONArray("img_placeholders_resources")).parse();
            return slide;
        } catch (Exception e) {
            return null;
        }
    }
}
