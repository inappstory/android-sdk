package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.content.Context;

public interface SimpleStoriesView {
    void pauseVideo();
    void playVideo();
    void stopVideo();
    void resumeVideo();
    Context getContext();
    void changeSoundStatus();
    void cancelDialog(String id);
    void sendDialog(String id, String data);
    void destroyView();
    float getCoordinate();
    void shareComplete(int stId, boolean success);
    void freezeUI();
    void setStoriesView(SimpleStoriesView storiesView);
    void checkIfClientIsSet();
    StoriesViewManager getManager();
}
