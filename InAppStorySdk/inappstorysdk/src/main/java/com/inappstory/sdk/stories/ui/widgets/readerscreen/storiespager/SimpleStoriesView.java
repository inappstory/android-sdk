package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.content.Context;

public interface SimpleStoriesView {
    void slidePause();
    void slideStart();
    void restartVideo();
    void stopVideo();
    void swipeUp();
    void loadJsApiResponse(String result, String cb);
    void resumeVideo();
    void clearSlide(int index);
    Context getContext();
    void changeSoundStatus();
    void cancelDialog(String id);
    void sendDialog(String id, String data);
    void destroyView();
    float getCoordinate();
    void shareComplete(String stId, boolean success);
    void freezeUI();
    void setStoriesView(SimpleStoriesView storiesView);
    void checkIfClientIsSet();
    void screenshotShare();
    void goodsWidgetComplete(String widgetId);
    StoriesViewManager getManager();
}
