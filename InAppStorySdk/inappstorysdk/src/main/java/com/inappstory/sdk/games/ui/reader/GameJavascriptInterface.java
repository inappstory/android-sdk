package com.inappstory.sdk.games.ui.reader;

import static com.inappstory.sdk.utils.DebugUtils.getMethodName;

import android.webkit.JavascriptInterface;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.games.domain.reader.IGameReaderViewModel;
import com.inappstory.sdk.games.domain.reader.IGameReaderViewModelJS;

public class GameJavascriptInterface {
    private final IGameReaderViewModelJS gameReaderViewModel;

    public GameJavascriptInterface(
            IASCore core,
            IGameReaderViewModelJS gameReaderViewModel
    ) {
        this.gameReaderViewModel = gameReaderViewModel;
    }

    @JavascriptInterface
    public int pausePlaybackOtherApp() {
        return gameReaderViewModel.pausePlaybackOtherApp();
    }

    @JavascriptInterface
    public void openUrl(String data) {
        gameReaderViewModel.openUrl(data);
    }

    @JavascriptInterface
    public void vibrate(int[] vibratePattern) {
        gameReaderViewModel.vibrate(vibratePattern);
    }

    @JavascriptInterface
    public void gameInstanceSetLocalData(String gameInstanceId, String data, boolean sendToServer) {
        gameReaderViewModel.gameInstanceSetLocalData(data, sendToServer);
    }

    @JavascriptInterface
    public String gameInstanceGetLocalData(String gameInstanceId) {
        return gameReaderViewModel.gameInstanceGetLocalData();
    }

    @JavascriptInterface
    public void event(
            String name,
            String data
    ) {
        gameReaderViewModel.jsEvent(name, data);
        logMethod("name:" + name + " | data:" + data);
    }

    @JavascriptInterface
    public void gameShouldForegroundCallback(String data) {
        gameReaderViewModel.gameShouldForegroundCallback(data);
        logMethod(data);
    }


    @JavascriptInterface
    public void gameLoaded() {
        gameReaderViewModel.gameLoaded();
        logMethod("");
    }

    private void logMethod(String payload) {
        gameReaderViewModel.logMethod(payload);
    }

    @JavascriptInterface
    public void gameLoadFailed(String reason, boolean canTryReload) {
        gameReaderViewModel.gameLoadFailed(reason, canTryReload);
        logMethod("reason:" + reason + " | canTryReload:" + canTryReload);
    }

    @JavascriptInterface
    public void reloadGameReader() {
        gameReaderViewModel.reloadGameReader();
        logMethod("null");
    }

    @JavascriptInterface
    public void initUserAccelerationSensor(String options) {
        gameReaderViewModel.initUserAccelerationSensor(options);
        logMethod(options);
    }

    @JavascriptInterface
    public void startUserAccelerationSensor() {
        gameReaderViewModel.startUserAccelerationSensor();
        logMethod("null");
    }

    @JavascriptInterface
    public void stopUserAccelerationSensor() {
        gameReaderViewModel.stopUserAccelerationSensor();
        logMethod("null");
    }

    @JavascriptInterface
    public void sendApiRequest(String data) {
        gameReaderViewModel.sendApiRequest(data);
        logMethod(data);
    }


    @JavascriptInterface
    public void setAudioManagerMode(String mode) {
        gameReaderViewModel.setAudioManagerMode(mode);
    }

    @JavascriptInterface
    public void gameComplete(String data, String eventData, String urlOrOptions) {
        gameReaderViewModel.gameComplete(data, urlOrOptions, eventData);
        logMethod("data:" + data + " | deeplink:" + urlOrOptions + " | eventData:" + eventData);
    }

    @JavascriptInterface
    public void gameStatisticEvent(String name, String data) {
        gameReaderViewModel.gameStatisticEvent(name, data);
        logMethod("name:" + name + " | data:" + data);
    }

    @JavascriptInterface
    public void showGoodsWidget(String id, String skus) {
        logMethod("id:" + id + " | data:" + skus);
        gameReaderViewModel.showGoodsWidget(skus, id);
    }


    @JavascriptInterface
    public void share(String id, String data) {
        gameReaderViewModel.share(id, data);
    }


    @JavascriptInterface
    public void openFilePicker(String data) {
        gameReaderViewModel.openFilePicker(data);
    }

    @JavascriptInterface
    public boolean hasFilePicker() {
        return gameReaderViewModel.hasFilePicker();
    }
}
