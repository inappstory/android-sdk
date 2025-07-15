package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;

public interface IBannersLoadCallback extends IASCallback {
    void allLoaded();

    void loadError();

    void isEmpty();
}
