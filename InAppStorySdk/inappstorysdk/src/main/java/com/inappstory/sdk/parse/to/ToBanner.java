package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Banner;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;


public final class ToBanner extends ToModelParser<Banner> {
    public ToBanner(String value) {
        super(value);
    }

    public ToBanner(JSONObject value) {
        super(value);
    }

    @Override
    protected Banner parseObject(JSONObject jsonObject) {
        try {
            return parseWithException(new ExtendedJsonObject(jsonObject));
        } catch (Exception e) {
            return null;
        }
    }

    private Banner parseWithException(ExtendedJsonObject jsonObj) throws Exception {
        Banner banner = new Banner();
        banner.id = jsonObj.getInt(ModelFields.Banner.id);
        banner.hasLimit = jsonObj.optBoolean(ModelFields.Banner.hasLimit);
        banner.frequencyLimit = jsonObj.optLongNullable(ModelFields.Banner.frequencyLimit);
        banner.displayTo = jsonObj.optLongNullable(ModelFields.Banner.displayTo);
        banner.displayFrom = jsonObj.optLongNullable(ModelFields.Banner.displayFrom);
        banner.hasSwipeUp = jsonObj.optBooleanNullable(ModelFields.Banner.hasSwipeUp);
        banner.disableClose = jsonObj.optBoolean(ModelFields.Banner.disableClose);

        banner.slides = new ToBannerSlideList(jsonObj.optJSONArray(
                ModelFields.Banner.slides
        )).parse();

        banner.customAttributes = JsonParser.toMap(jsonObj.optJSONObject(
                ModelFields.Banner.customAttributes));
        banner.layoutTemplateVariables = JsonParser.toMap(jsonObj.optJSONObject(
                ModelFields.Banner.layoutTemplateVariables
        ));
        banner.appearance = JsonParser.toObjectMap(jsonObj.optJSONObject(
                ModelFields.Banner.appearance
        ));

        return banner;
    }

    @Override
    protected Banner parseString(@NonNull String value) {
        try {
            return parseWithException(new ExtendedJsonObject(new JSONObject(value)));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "Banner";
    }
}
