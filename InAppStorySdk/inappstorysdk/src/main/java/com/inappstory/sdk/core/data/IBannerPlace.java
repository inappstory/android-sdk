package com.inappstory.sdk.core.data;

import java.util.List;

public interface IBannerPlace<T> {
    int id();
    List<T> banners();
}
