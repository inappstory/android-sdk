package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.core.data.IReaderContent;

import java.util.List;

public interface LoadBannerPlaceCallback {
    void success(List<IBanner> content);
    void isEmpty();
    void error();
}
