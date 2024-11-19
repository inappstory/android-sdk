package com.inappstory.sdk.inappmessage.ui.reader;

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
        slideViewModel.storyStatisticEvent(name, data, eventData);
    }

    @JavascriptInterface
    public void storyStatisticEvent(
            String name,
            String data
    ) {
        slideViewModel.storyStatisticEvent(name, data);
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
    public void storySendData(String data) {
        slideViewModel.storyLoadingFailed(data);
    }

    @JavascriptInterface
    public void storySetLocalData(String data, boolean sendToServer) {
        slideViewModel.storySetLocalData(data, sendToServer);
    }


    @JavascriptInterface
    public String storyGetLocalData() {
        return slideViewModel.storyGetLocalData();
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
