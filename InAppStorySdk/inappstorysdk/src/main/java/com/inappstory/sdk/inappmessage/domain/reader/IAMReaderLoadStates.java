package com.inappstory.sdk.inappmessage.domain.reader;

public enum IAMReaderLoadStates {
    EMPTY,
    ASSETS_LOADING,
    ASSETS_LOADED,
    ASSETS_FAILED,
    CONTENT_LOADING,
    CONTENT_LOADED,
    RENDER_READY,
    CONTENT_FAILED,
    CONTENT_FAILED_CLOSE
}
