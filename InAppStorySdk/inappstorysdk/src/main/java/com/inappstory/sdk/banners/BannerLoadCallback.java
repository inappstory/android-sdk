package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;

public interface BannerLoadCallback extends IASCallback {
    void loaded(int id);

    void allLoaded();

    void loadError(int id);

    void loadError();

    void isEmpty();
}
