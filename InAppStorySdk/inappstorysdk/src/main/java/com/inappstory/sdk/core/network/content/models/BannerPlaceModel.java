package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.core.data.IBannerPlaceModel;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.util.ArrayList;
import java.util.List;

public class BannerPlaceModel implements IBannerPlaceModel<Banner> {

    @SerializedName(ModelFields.BannerPlaceModel.banners)
    public List<Banner> banners;


    @SerializedName(ModelFields.BannerPlaceModel.id)
    public int id;

    @Override
    public int id() {
        return id;
    }

    @Override
    public List<Banner> banners() {
        if (banners == null) banners = new ArrayList<>();
        return banners;
    }
}
