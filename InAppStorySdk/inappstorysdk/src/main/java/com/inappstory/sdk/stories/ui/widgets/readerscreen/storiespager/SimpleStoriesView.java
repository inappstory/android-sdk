package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.content.Context;

import com.inappstory.sdk.core.IASCore;

public interface SimpleStoriesView {
    void pauseSlide();
    void startSlide(IASCore core);
    void restartSlide(IASCore core);
    void stopSlide();
    void swipeUp();

    void clearSlide(int index);
    void loadJsApiResponse(String result, String cb);
    void resumeSlide();
    Context getActivityContext();
    void changeSoundStatus(IASCore core);
    void cancelDialog(String id);
    void sendDialog(String id, String data);
    void destroyView();
    float getCoordinate();
    void shareComplete(String stId, boolean success);
    void freezeUI();
    void setStoriesView(SimpleStoriesView storiesView);
    void checkIfClientIsSet();
    void screenshotShare(String id);
    void goodsWidgetComplete(String widgetId);
    StoriesViewManager getManager();
}
