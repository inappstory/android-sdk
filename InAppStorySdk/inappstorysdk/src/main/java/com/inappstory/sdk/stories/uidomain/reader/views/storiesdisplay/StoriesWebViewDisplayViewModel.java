package com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay;

import androidx.lifecycle.LiveData;

import com.inappstory.sdk.game.reader.GameLaunchData;
import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;

public class StoriesWebViewDisplayViewModel implements IStoriesWebViewDisplayViewModel {
    public StoriesWebViewDisplayViewModel(IStoriesReaderPageViewModel pageViewModel) {
        this.pageViewModel = pageViewModel;
    }

    private IStoriesReaderPageViewModel pageViewModel;

    @Override
    public void freezeUI() {

    }

    @Override
    public void unfreezeUI() {

    }

    @Override
    public boolean isUIFrozen() {
        return false;
    }

    @Override
    public StoryDisplayState getStoryDisplayState() {
        return null;
    }

    @Override
    public void storyClick(String payload) {

    }

    @Override
    public void slideLoadError(int index) {

    }

    @Override
    public void changeIndex(int index) {

    }

    @Override
    public void showStorySlide(int id, int index) {

    }

    @Override
    public void sendApiRequest(String data) {

    }

    @Override
    public void openGameReaderWithoutGameCenter(GameLaunchData launchData) {

    }

    @Override
    public void openGameReaderFromGameCenter(String gameInstanceId) {

    }

    @Override
    public void setAudioManagerMode(String mode) {

    }

    @Override
    public void storyShowNext() {

    }

    @Override
    public void storyShowPrev() {

    }

    @Override
    public void resetTimers() {

    }

    @Override
    public void nextSlide() {

    }

    @Override
    public void restartSlideWithDuration(long duration) {

    }

    @Override
    public void storyShowTextInput(String id, String data) {

    }

    @Override
    public void storyStarted() {

    }

    @Override
    public void storyResumed(double startTime) {

    }

    @Override
    public void storyLoaded(int slideIndex) {

    }

    @Override
    public void storyStatisticEvent(String name, String data, String eventData) {

    }

    @Override
    public void share(String id, String data) {

    }

    @Override
    public void pauseUI() {

    }

    @Override
    public void resumeUI() {

    }

    @Override
    public void storySendData(String data) {

    }

    @Override
    public void storySetLocalData(String data, boolean sendToServer) {

    }

    @Override
    public String storyGetLocalData() {
        return null;
    }

    @Override
    public void pauseSlide() {

    }

    @Override
    public void resumeSlide() {

    }

    @Override
    public void lockStoriesDisplayContainer() {

    }

    @Override
    public void setStateAsLoaded() {

    }

    @Override
    public LiveData<String> evaluateJSCalls() {
        return null;
    }

    @Override
    public LiveData<String> loadUrlCalls() {
        return null;
    }

    @Override
    public LiveData<SlideContentState> slideContentState() {
        return null;
    }
}
