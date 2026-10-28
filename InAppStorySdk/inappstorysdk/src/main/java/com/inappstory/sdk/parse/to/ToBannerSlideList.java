package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.BannerSlide;
import com.inappstory.sdk.core.network.content.models.StorySlide;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToBannerSlideList extends ToListParser<BannerSlide, ToBannerSlide> {
    @Override
    protected ToBannerSlide createParserInstance(JSONObject object) {
        return new ToBannerSlide(object);
    }

    public ToBannerSlideList(JSONArray value) {
        super(value);
    }
}
