package com.inappstory.sdk.core.data;

import java.util.List;

public interface IBannerPlaceModel<T> {
    int id();
    List<T> banners();
}
