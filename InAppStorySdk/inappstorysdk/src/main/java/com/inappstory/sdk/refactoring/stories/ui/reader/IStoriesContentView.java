package com.inappstory.sdk.refactoring.stories.ui.reader;


public interface IStoriesContentView {
    void gameComplete(String data);
    void setClientVariables(String extraOptions);
    void handleBackPress(String extraOptions);
    void cartUpdatedResultSuccess(String successCB, String requestId, String cardStr);
    void cartUpdatedResultError(String errorCB, String requestId, String reason);
    void clearSlide(int index);
    void layoutAndSlide(String layout, String slide);
    void startSlide(boolean soundOn);
    void restartSlide(boolean soundOn);
    void pauseSlide();
    void resumeSlide();
    void stopSlide(boolean newPage);
    void autoSlideEnd();
    void swipeUp();
    void loadJsApiResponse(String result, String cb);
    void changeSoundStatus(boolean soundOn);
    void cancelDialog(String id);
    void sendDialog(String id, String data);
    void shareComplete(String id, boolean success);
    void screenshotShare(String shareId);
    void goodsWidgetComplete(String widgetId);
}
