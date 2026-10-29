package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.BannerPlaceModel;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToBannerPlaceModel extends ToModelParser<BannerPlaceModel> {
    public ToBannerPlaceModel(String value) {
        super(value);
    }

    @Override
    protected BannerPlaceModel parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            BannerPlaceModel bannerPlaceModel = new BannerPlaceModel();
            bannerPlaceModel.id = jsonObj.getInt("id");
            bannerPlaceModel.banners = new ToBannerList(jsonObj.optJSONArray("banners")).parse();
            return bannerPlaceModel;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "BannerPlaceModel";
    }
}
