package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Banner;
import com.inappstory.sdk.core.network.content.models.Image;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ToBannerList extends ToListParser<Banner, ToBanner> {
    @Override
    protected ToBanner createParserInstance(JSONObject object) {
        return new ToBanner(object);
    }

    public ToBannerList(JSONArray value) {
        super(value);
    }
}
