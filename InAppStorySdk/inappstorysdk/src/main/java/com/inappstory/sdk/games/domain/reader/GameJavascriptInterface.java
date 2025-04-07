package com.inappstory.sdk.games.domain.reader;

import android.webkit.JavascriptInterface;

public class GameJavascriptInterface {
    private final IGameReaderViewModelJS viewModelJS;

    public GameJavascriptInterface(IGameReaderViewModelJS viewModelJS) {
        this.viewModelJS = viewModelJS;
    }

    void logMethod(String payload) {
        viewModelJS.logMethod(payload);
    }

    @JavascriptInterface
    public int pausePlaybackOtherApp() {
        return viewModelJS.pausePlaybackOtherApp();
    }

    @JavascriptInterface
    public void openUrl(String data) {
        viewModelJS.openUrl(data);
    }

    @JavascriptInterface
    public void vibrate(int[] vibratePattern) {
        viewModelJS.vibrate(vibratePattern);
    }

    @JavascriptInterface
    public void gameInstanceSetLocalData(String data, boolean sendToServer) {
        viewModelJS.gameInstanceSetLocalData(data, sendToServer);
    }

    @JavascriptInterface
    public String gameInstanceGetLocalData() {
        return viewModelJS.gameInstanceGetLocalData();
    }

    @JavascriptInterface
    public void jsEvent(
            String name,
            String data
    ) {
        viewModelJS.jsEvent(name, data);
    }

    @JavascriptInterface
    public void gameShouldForegroundCallback(String data) {
        viewModelJS.gameShouldForegroundCallback(data);
    }

    @JavascriptInterface
    public void gameLoaded() {
        viewModelJS.gameLoaded();
    }

    @JavascriptInterface
    public void gameLoadFailed(String reason, boolean canTryReload) {
        viewModelJS.gameLoadFailed(reason, canTryReload);
    }

    @JavascriptInterface
    public void reloadGameReader() {
        viewModelJS.reloadGameReader();
    }

    @JavascriptInterface
    public void initUserAccelerationSensor(String options) {
        viewModelJS.initUserAccelerationSensor(options);
    }

    @JavascriptInterface
    public void startUserAccelerationSensor() {
        viewModelJS.startUserAccelerationSensor();
    }

    @JavascriptInterface
    public void stopUserAccelerationSensor() {
        viewModelJS.stopUserAccelerationSensor();
    }

    @JavascriptInterface
    public void sendApiRequest(String data) {
        viewModelJS.sendApiRequest(data);
    }

    @JavascriptInterface
    public void setAudioManagerMode(String mode) {
        viewModelJS.setAudioManagerMode(mode);
    }

    @JavascriptInterface
    public void gameComplete(String data, String eventData, String urlOrOptions) {
        viewModelJS.gameComplete(data, eventData, urlOrOptions);
    }

    @JavascriptInterface
    public void gameStatisticEvent(String name, String data) {
        viewModelJS.gameStatisticEvent(name, data);
    }

    @JavascriptInterface
    public void showGoodsWidget(String id, String skus) {
        viewModelJS.showGoodsWidget(id, skus);
    }

    @JavascriptInterface
    public void share(String id, String data) {
        viewModelJS.share(id, data);
    }

    @JavascriptInterface
    public void openFilePicker(String data) {
        viewModelJS.openFilePicker(data);
    }

    @JavascriptInterface
    public boolean hasFilePicker() {
        return viewModelJS.hasFilePicker();
    }
}
