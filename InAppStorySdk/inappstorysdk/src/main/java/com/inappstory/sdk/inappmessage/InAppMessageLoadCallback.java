package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.core.api.IASCallback;

public interface InAppMessageLoadCallback extends IASCallback {
    void loaded(int id);

    void allLoaded();

    void loadError(int id);

    void loadError();

    void isEmpty();
}
