package com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay;

import com.inappstory.sdk.game.reader.GameLaunchData;

public interface IStoriesDisplayViewModel {
    void freezeUI();
    void unfreezeUI();
    boolean isUIFrozen();
    StoryDisplayState getStoryDisplayState();

    void storyClick(String payload);
    void slideLoadError(int index);
    void changeIndex(int index);
    void showStorySlide(int id, int index);
    void sendApiRequest(String data);
    void openGameReaderWithoutGameCenter(
            GameLaunchData launchData
    );
    void openGameReaderFromGameCenter(
            String gameInstanceId
    );
    void setAudioManagerMode(String mode);
    void storyShowNext();
    void storyShowPrev();
    void resetTimers();
    void nextSlide();
    void restartSlideWithDuration(long duration);
    void storyShowTextInput(String id, String data);

    void storyStarted();
    void storyResumed(double startTime);
    void storyLoaded(int slideIndex);
    void storyStatisticEvent(
            String name,
            String data,
            String eventData
    );
    void share(String id, String data);
    void pauseUI();
    void resumeUI();
    void storySendData(String data);
    void storySetLocalData(String data, boolean sendToServer);
    String storyGetLocalData();
    void pauseSlide();
    void resumeSlide();
    void lockStoriesDisplayContainer();

    void setStateAsLoaded();

}
