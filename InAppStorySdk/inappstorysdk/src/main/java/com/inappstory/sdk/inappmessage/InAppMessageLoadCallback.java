package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.core.api.IASCallback;

public interface InAppMessageLoadCallback extends IASCallback {
    void loaded(String id);

    void allLoaded();

    void loadError(String id);

    void loadError();

    void isEmpty();
}
