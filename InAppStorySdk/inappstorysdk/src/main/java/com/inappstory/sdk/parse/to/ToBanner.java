package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Banner;
import com.inappstory.sdk.network.JsonParser;
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
            banner.id = jsonObj.getInt("id");
            banner.hasLimit = jsonObj.optBoolean("has_limit");
            banner.layout = jsonObj.optStringNullable("layout");
            banner.frequencyLimit = jsonObj.optLongNullable("frequency_limit");
            banner.displayTo = jsonObj.optLongNullable("display_to");
            banner.displayFrom = jsonObj.optLongNullable("display_from");
            banner.hasSwipeUp = jsonObj.optBooleanNullable("has_swipe_up");
            banner.disableClose = jsonObj.optBoolean("disable_close");

            banner.slides = new ToBannerSlideList(jsonObj.optJSONArray("slides")).parse();

            banner.customAttributes = JsonParser.toMap(jsonObj.optJSONObject("custom_attributes"));
            banner.layoutTemplateVariables = JsonParser.toMap(jsonObj.optJSONObject("layout_template_variables"));
            banner.appearance = JsonParser.toObjectMap(jsonObj.optJSONObject("appearance"));

            return banner;
        } catch (Exception e) {
            return null;
        }
    }
}
