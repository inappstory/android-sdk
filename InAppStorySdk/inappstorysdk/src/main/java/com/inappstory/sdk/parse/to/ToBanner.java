package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Banner;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToBanner extends ToModelParser<Banner> {
    public ToBanner(String value) {
        super(value);
    }

    @Override
    protected Banner parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            Banner banner = new Banner();
            story.id = jsonObj.getInt("id");
            return banner;
        } catch (Exception e) {
            return null;
        }
    }
}
