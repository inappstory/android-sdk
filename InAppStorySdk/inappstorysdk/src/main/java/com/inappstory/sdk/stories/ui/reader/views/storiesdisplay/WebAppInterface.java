package com.inappstory.sdk.stories.ui.reader.views.storiesdisplay;

import android.webkit.JavascriptInterface;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.models.js.StoryIdSlideIndex;
import com.inappstory.sdk.core.utils.network.JsonParser;
import com.inappstory.sdk.game.reader.GameLaunchData;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.IStoriesDisplayViewModel;

public class WebAppInterface {
    IStoriesDisplayViewModel viewModel;

    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(IStoriesDisplayViewModel viewModel) {
        this.viewModel = viewModel;
    }

    static String getMethodName() {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[4].getMethodName();
    }

    private void logMethod(String payload) {
        InAppStoryManager.showDLog(
                "JS_method_test",
                viewModel.getStoryDisplayState().storyId() + " " +
                        viewModel.getStoryDisplayState().slideIndex() + " " +
                        getMethodName() + " " +
                        payload
        );
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void storyClick(String payload) {
        viewModel.storyClick(payload);
        logMethod(payload);
    }

    @JavascriptInterface
    public void storyLoadingFailed(String data) {
        if (data != null) {
            StoryIdSlideIndex loadedData = JsonParser.fromJson(data, StoryIdSlideIndex.class);
            viewModel.slideLoadError(loadedData.index);
        }
        logMethod("");
    }

    @JavascriptInterface
    public void storyShowSlide(int index) {
        viewModel.changeIndex(index);
        logMethod("" + index);
    }

    @JavascriptInterface
    public void showSingleStory(int id, int index) {
        logMethod("" + id + " " + index);
        viewModel.showStorySlide(id, index);
    }

    @JavascriptInterface
    public void sendApiRequest(String data) {
        viewModel.sendApiRequest(data);
    }

    @JavascriptInterface
    public void openGameReader(
            String gameUrl,
            String splashScreenPath,
            String gameConfig,
            String resources,
            String options
    ) {
        viewModel.openGameReaderWithoutGameCenter(
                new GameLaunchData(
                        gameUrl,
                        splashScreenPath,
                        gameConfig,
                        resources,
                        options
                )
        );
        logMethod(gameUrl);
    }

    @JavascriptInterface
    public void openGame(String gameInstanceId) {
        viewModel.openGameReaderFromGameCenter(gameInstanceId);
        logMethod(gameInstanceId);
    }

    @JavascriptInterface
    public void openGameReader(String gameUrl, String splashScreenPath,
                               String gameConfig, String resources) {
        viewModel.openGameReaderWithoutGameCenter(
                new GameLaunchData(
                        gameUrl,
                        splashScreenPath,
                        gameConfig,
                        resources,
                        null
                )
        );
        logMethod(gameUrl);
    }


    @JavascriptInterface
    public void setAudioManagerMode(String mode) {
        viewModel.setAudioManagerMode(mode);
        logMethod(mode);
    }


    @JavascriptInterface
    public void storyShowNext() {
        viewModel.storyShowNext();
        logMethod("");
    }

    @JavascriptInterface
    public void storyShowPrev() {
        viewModel.storyShowPrev();
        logMethod("");
    }

    @JavascriptInterface
    public void resetTimers() {
        viewModel.resetTimers();
        logMethod("");
    }

    @JavascriptInterface
    public void storyShowNextSlide(long delay) {
        if (delay != 0) {
            viewModel.restartSlideWithDuration(delay);
        } else {
            viewModel.nextSlide();
        }
        logMethod("" + delay);
    }

    @JavascriptInterface
    public void storyShowTextInput(String id, String data) {
        viewModel.storyShowTextInput(id, data);
        logMethod("");
    }

    @JavascriptInterface
    public void storyStarted() {
        viewModel.storyStarted();
        logMethod("");
    }

    @JavascriptInterface
    public void storyStarted(double startTime) {
        viewModel.storyStarted();
        logMethod("" + startTime);
    }

    @JavascriptInterface
    public void storyResumed(double startTime) {
        viewModel.storyResumed(startTime);

        logMethod("" + startTime);
    }

    @JavascriptInterface
    public void storyLoaded() {
        viewModel.storyLoaded(-1);
        logMethod("");
    }

    @JavascriptInterface
    public void storyLoaded(String data) {
        if (data != null) {
            int slideIndex = JsonParser.fromJson(data, StoryIdSlideIndex.class).index;
            viewModel.storyLoaded(slideIndex);
        } else {
            viewModel.storyLoaded(-1);
        }
        logMethod(data + "");
    }


    @JavascriptInterface
    public void storyStatisticEvent(
            String name,
            String data,
            String eventData
    ) {
        viewModel.storyStatisticEvent(name, data, eventData);
        logMethod(name + " " + data + " " + eventData);
    }

    @JavascriptInterface
    public void storyStatisticEvent(
            String name,
            String data
    ) {
        viewModel.storyStatisticEvent(name, data, data);
        logMethod(name + " " + data);
    }

    @JavascriptInterface
    public void emptyLoaded() {
        logMethod("");
    }

    @JavascriptInterface
    public void share(String id, String data) {
        viewModel.share(id, data);
        logMethod(id + " " + data);
    }

    @JavascriptInterface
    public void storyFreezeUI() {
        viewModel.freezeUI();
        logMethod("");
    }

    @JavascriptInterface
    public void storyPauseUI() {
        viewModel.pauseUI();
        logMethod("");
    }

    @JavascriptInterface
    public void storyResumeUI() {
        viewModel.resumeUI();
        logMethod("");
    }


    @JavascriptInterface
    public void storySendData(String data) {
        viewModel.storySendData(data);
        logMethod(data);
    }

    @JavascriptInterface
    public void storySetLocalData(String data, boolean sendToServer) {
        viewModel.storySetLocalData(data, sendToServer);
        logMethod(data + " " + sendToServer);
    }


    @JavascriptInterface
    public String storyGetLocalData() {
        String res = viewModel.storyGetLocalData();
        logMethod(res != null ? res : "");
        return res == null ? "" : res;
    }


    @JavascriptInterface
    public void defaultTap(String val) {
        logMethod(val);
    }
}