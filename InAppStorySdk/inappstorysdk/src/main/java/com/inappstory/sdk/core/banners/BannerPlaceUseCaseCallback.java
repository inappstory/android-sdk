package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.data.IBanner;

import java.util.List;

public interface BannerPlaceUseCaseCallback {
    void success(List<IBanner> content);
    void isEmpty();
    void error();
}
