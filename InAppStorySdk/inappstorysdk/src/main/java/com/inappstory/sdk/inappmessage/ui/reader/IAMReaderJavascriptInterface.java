package com.inappstory.sdk.inappmessage.ui.reader;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderSlideViewModel;

public class IAMReaderJavascriptInterface {
    private final IIAMReaderSlideViewModel slideViewModel;

    public IAMReaderJavascriptInterface(IIAMReaderSlideViewModel viewModel) {
        this.slideViewModel = viewModel;
    }

    @JavascriptInterface
    public void storyClick(String payload) {
        slideViewModel.slideClick(payload);
    }

    @JavascriptInterface
    public void updateTimeline(String data) {
        slideViewModel.updateTimeline(data);
    }

    @JavascriptInterface
    public void storyRenderReady() {
        slideViewModel.renderReady();
        Log.e("JS_method_test", "storyRenderReady");
    }

    @JavascriptInterface
    public void storyLoadingFailed(String data) {
        slideViewModel.storyLoadingFailed(data);
    }

    @JavascriptInterface
    public void storyShowSlide(int index) {
        slideViewModel.storyShowSlide(index);
    }

    @JavascriptInterface
    public void showSingleStory(int id, int index) {
        slideViewModel.showSingleStory(id, index);
    }

    @JavascriptInterface
    public void sendApiRequest(String data) {
        slideViewModel.sendApiRequest(data);
    }

    @JavascriptInterface
    public void onCardLoadingStateChange(int state, String reason) {
        slideViewModel.onCardLoadingStateChange(state, reason);
        Log.e("JS_method_test", "onCardLoadingStateChange " + state + " " + reason);
    }

    @JavascriptInterface
    public void onEvent(String name, String event) {
        slideViewModel.onEvent(name, event);

        Log.e("JS_method_test", "onEvent " + name + " " + event);
    }

    @JavascriptInterface
    public void vibrate(int[] vibratePattern) {
        slideViewModel.vibrate(vibratePattern);
    }

    @JavascriptInterface
    public void openGame(String gameInstanceId) {
        slideViewModel.openGame(gameInstanceId);
    }

    @JavascriptInterface
    public void setAudioManagerMode(String mode) {
        slideViewModel.setAudioManagerMode(mode);
    }


    @JavascriptInterface
    public void storyShowNext() {
        slideViewModel.storyShowNext();
    }

    @JavascriptInterface
    public void storyShowPrev() {
        slideViewModel.storyShowPrev();
    }

    @JavascriptInterface
    public void writeToClipboard(String payload) {
        slideViewModel.writeToClipboard(payload);
    }

    @JavascriptInterface
    public void storyShowNextSlide(long delay) {
        slideViewModel.storyShowNextSlide(delay);
    }

    @JavascriptInterface
    public void storyShowNextSlide() {
        slideViewModel.storyShowNextSlide();
    }

    @JavascriptInterface
    public void storyShowTextInput(String id, String data) {
        slideViewModel.storyShowTextInput(id, data);
    }

    @JavascriptInterface
    public void storyStarted() {
        slideViewModel.storyStarted();
    }

    @JavascriptInterface
    public void storyStarted(double startTime) {
        slideViewModel.storyStarted(startTime);
    }

    @JavascriptInterface
    public void storyLoaded() {
        slideViewModel.storyLoaded();
    }

    @JavascriptInterface
    public void storyLoaded(String data) {
        slideViewModel.storyLoaded(data);
    }


    @JavascriptInterface
    public void storyStatisticEvent(
            String name,
            String data,
            String eventData
    ) {
        slideViewModel.statisticEvent(name, data, eventData);
    }

    @JavascriptInterface
    public void emptyLoaded() {
        slideViewModel.emptyLoaded();
    }

    @JavascriptInterface
    public void share(String id, String data) {
        slideViewModel.share(id, data);
    }

    @JavascriptInterface
    public void storyFreezeUI() {
        slideViewModel.storyFreezeUI();
    }

    @JavascriptInterface
    public void closeStory(String reason) {
        slideViewModel.closeReader();
    }

    @JavascriptInterface
    public void storySendData(String data) {
        slideViewModel.storySendData(data);
    }

    @JavascriptInterface
    public void storySetLocalData(String data, boolean sendToServer) {
        slideViewModel.setLocalUserData(data, sendToServer);
    }


    @JavascriptInterface
    public String storyGetLocalData() {
        return slideViewModel.getLocalUserData();
    }

    @JavascriptInterface
    public void shareSlideScreenshotCb(String shareId, boolean result) {
        slideViewModel.shareSlideScreenshotCb(shareId, result);
    }


    @JavascriptInterface
    public void defaultTap(String val) {
        slideViewModel.defaultTap(val);
    }
}
