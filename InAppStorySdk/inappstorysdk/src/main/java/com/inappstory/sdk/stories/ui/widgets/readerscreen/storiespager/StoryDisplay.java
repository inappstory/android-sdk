package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.content.Context;

import com.inappstory.sdk.stories.ui.widgets.readerscreen.webview.DisableTouchEvent;

public interface StoryDisplay {
    void slidePause();
    void slideStart();
    void restartVideo();
    void stopVideo();
    void swipeUp();

    void clearSlide(int index);
    void loadJsApiResponse(String result, String cb);
    void resumeVideo();
    Context getContext();
    void changeSoundStatus();
    void cancelDialog(String id);
    void sendDialog(String id, String data);
    void destroyView();
    float getCoordinate();
    void shareComplete(String stId, boolean success);
    void freezeUI();
    void setStoriesView(StoryDisplay storiesView);
    void checkIfClientIsSet();
    void screenshotShare();
    void goodsWidgetComplete(String widgetId);
    StoriesViewManager getManager();

    void disableTouchEvent(DisableTouchEvent disableDispatchTouchEvent);
}
