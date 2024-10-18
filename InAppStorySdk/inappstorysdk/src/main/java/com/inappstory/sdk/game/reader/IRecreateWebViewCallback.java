package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.stories.ui.views.IASWebView;

public interface IRecreateWebViewCallback {
    void invoke(IASWebView oldWebView);
}