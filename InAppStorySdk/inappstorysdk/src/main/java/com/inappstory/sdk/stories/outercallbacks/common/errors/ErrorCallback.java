package com.inappstory.sdk.stories.outercallbacks.common.errors;

public interface ErrorCallback {
    void loadListError(String feed);
    void loadOnboardingError(String feed);
    void loadSingleError();
    void cacheError();
    void readerError();
    void emptyLinkError();
    void sessionError();
    void noConnection();
}
