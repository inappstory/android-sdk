package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.BannerSlide;
import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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

            slide.slideIndex = jsonObj.optInt(ModelFields.BannerSlide.slideIndex);
            slide.duration = jsonObj.optInt(ModelFields.BannerSlide.duration);
            slide.html = jsonObj.optString(ModelFields.BannerSlide.html);

            slide.slidePayload = new ToSlidePayload(jsonObj.optJSONObject(
                    ModelFields.BannerSlide.slidePayload
            )).parse();
            slide.resources = new ToContentResourceList(jsonObj.optJSONArray(
                    ModelFields.BannerSlide.resources
            )).parse();
            slide.placeholders = new ToContentResourceList(jsonObj.optJSONArray(
                    ModelFields.BannerSlide.placeholders
            )).parse();
            return slide;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "BannerSlide";
    }
}
