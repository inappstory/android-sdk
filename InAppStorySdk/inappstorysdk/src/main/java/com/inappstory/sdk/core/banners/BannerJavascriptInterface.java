package com.inappstory.sdk.core.banners;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class BannerJavascriptInterface {
    private final IBannerViewModel slideViewModel;

    public BannerJavascriptInterface(IBannerViewModel viewModel) {
        this.slideViewModel = viewModel;
    }

    @JavascriptInterface
    public void storyClick(String payload) {
        slideViewModel.slideClick(payload);
    }


    @JavascriptInterface
    public void writeToClipboard(String payload) {
        slideViewModel.writeToClipboard(payload);
    }

    @JavascriptInterface
    public void storyLoadingFailed(String data) {
        slideViewModel.slideLoadingFailed(data);
    }

    @JavascriptInterface
    public void updateTimeline(String data) {
        Log.e("BannerJS", "updateTimeline " + data);
        slideViewModel.updateTimeline(data);
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
    public void storyStarted() {
        slideViewModel.slideStarted(null);
    }

    @JavascriptInterface
    public void storyStarted(double startTime) {
        slideViewModel.slideStarted(startTime);
    }

    @JavascriptInterface
    public void storyLoaded() {
        slideViewModel.slideLoaded(null);
    }

    @JavascriptInterface
    public void storyLoaded(String data) {
        slideViewModel.slideLoaded(data);
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
    public void share(String id, String data) {
        slideViewModel.share(id, data);
    }

    @JavascriptInterface
    public void storyFreezeUI() {
        slideViewModel.freezeUI();
    }

    @JavascriptInterface
    public void storyUnfreezeUI() {
        slideViewModel.unfreezeUI();
    }

    @JavascriptInterface
    public void storySendData(String data) {
        slideViewModel.sendData(data);
    }

    @JavascriptInterface
    public void storyShowNext() {
        Log.e("BannerJS", "storyShowNext");
        slideViewModel.showNext();
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
}
