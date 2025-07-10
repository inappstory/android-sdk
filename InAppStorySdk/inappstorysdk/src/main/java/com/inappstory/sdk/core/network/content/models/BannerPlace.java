package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.core.data.IBannerPlace;
import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BannerPlace implements IBannerPlace<Banner> {

    @SerializedName("banners")
    public List<Banner> banners;


    @SerializedName("id")
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
