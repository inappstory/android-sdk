package com.inappstory.sdk.stories.outercallbacks.common.errors;

public interface ErrorCallback {
    void loadListError(String feed);
    void cacheError();
    void emptyLinkError();
    void sessionError();
    void noConnection();
}
