package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.data.IBanner;

import java.util.List;

public interface IBannerWidgetState {
    List<String> tags();

    String iterationId();

    BannersWidgetLoadStates loadState();

    List<IBanner> getItems();

    String placeId();

    IBannerWidgetState copy();

    IBannerWidgetState iterationId(String iterationId);

    IBannerWidgetState loadState(BannersWidgetLoadStates loadState);

    IBannerWidgetState items(List<IBanner> items);

    IBannerWidgetState tags(List<String> tags);

    IBannerWidgetState placeId(String placeId);
}
