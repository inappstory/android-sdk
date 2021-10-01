package com.inappstory.sdk.stories.outercallbacks.common.errors;

public interface ErrorCallback {
    void loadListError();
    void loadOnboardingError();
    void loadSingleError();
    void cacheError();
    void readerError();
    void emptyLinkError();
    void sessionError();
    void noConnection();
}
