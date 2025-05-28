package com.inappstory.sdk.stories.utils;

public interface ShowGoodsCallback {
    void goodsIsOpened();
    void goodsIsClosed(String widgetId);
    void goodsIsCanceled(String widgetId);
}
