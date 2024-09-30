package com.inappstory.sdk.stories.outercallbacks.common.errors;

import com.inappstory.sdk.core.api.IASCallback;

public interface ErrorCallback extends IASCallback {
    void loadListError(String feed);
    void cacheError();
    void emptyLinkError();
    void sessionError();
    void noConnection();
}
