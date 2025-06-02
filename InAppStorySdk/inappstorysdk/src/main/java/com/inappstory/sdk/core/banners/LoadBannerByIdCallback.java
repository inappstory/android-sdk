package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.data.IReaderContent;

public interface LoadBannerByIdCallback {
    void success(IReaderContent content);
    void error();
}
