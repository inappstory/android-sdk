package com.inappstory.sdk.game.reader;

import static com.inappstory.sdk.utils.DebugUtils.getMethodName;

import android.webkit.JavascriptInterface;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.utils.KeyValueStorage;


public class GameJSInterface {
    GameManager manager;

    /**
     * Instantiate the interface and set the context
     */
    GameJSInterface(GameManager gameManager) {
        this.manager = gameManager;
    }

    /**
     * Show a toast from the web page
     */


    @JavascriptInterface
    public int pausePlaybackOtherApp() {
        return manager.pausePlaybackOtherApp();
    }

    @JavascriptInterface
    public void storySetLocalData(int storyId, String data, boolean sendToServer) {
        manager.storySetData(data, sendToServer);
    }

    @JavascriptInterface
    public void openUrl(String data) {
        manager.openUrl(data);
    }

    @JavascriptInterface
    public void vibrate(int[] vibratePattern) {
        manager.vibrate(vibratePattern);
    }

    @JavascriptInterface
    public void gameInstanceSetLocalData(String gameInstanceId, String data, boolean sendToServer) {
        manager.gameInstanceSetData(gameInstanceId, data, sendToServer);
    }

    @JavascriptInterface
    public String gameInstanceGetLocalData(String gameInstanceId) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return "";
        String id = gameInstanceId;
        if (id == null) id = manager.gameCenterId;
        if (id == null) return "";
        String res = KeyValueStorage.getString("gameInstance_" + id
                + "__" + service.getUserId());
        return res == null ? "" : res;
    }

    @JavascriptInterface
    public String storyGetLocalData(int storyId) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return "";
        String res = KeyValueStorage.getString("story" + storyId
                + "__" + service.getUserId());
        return res == null ? "" : res;
    }

    @JavascriptInterface
    public void event(
            String name,
            String data
    ) {
        manager.jsEvent(name,  data);
        logMethod("name:" + name + " | data:" + data);
    }

    @JavascriptInterface
    public void gameShouldForegroundCallback(String data) {
        manager.gameShouldForegroundCallback(data);
        logMethod(data);
    }


    @JavascriptInterface
    public void gameLoaded() {
        manager.gameLoaded();
        logMethod("");
    }

    private void logMethod(String payload) {
        InAppStoryManager.showDLog("JS_game_method_test",
                manager.gameCenterId + " " + getMethodName() + " " + payload);
    }

    @JavascriptInterface
    public void gameLoadFailed(String reason, boolean canTryReload) {
        manager.gameLoadFailed(reason, canTryReload);
        logMethod("reason:" + reason + " | canTryReload:" + canTryReload);
    }

    @JavascriptInterface
    public void reloadGameReader() {
        manager.clearTries();
        manager.reloadGame();
        logMethod("null");
    }

    @JavascriptInterface
    public void sendApiRequest(String data) {
        manager.sendApiRequest(data);
        logMethod(data);
    }

    @JavascriptInterface
    public void gameComplete(String data) {
        manager.gameCompleted(data, null, null);
        logMethod(data);
    }

    @JavascriptInterface
    public void setAudioManagerMode(String mode) {
        manager.setAudioManagerMode(mode);
    }

    @JavascriptInterface
    public void gameComplete(String data, String eventData, String deeplink) {
        manager.gameCompleted(data, deeplink, eventData);
        logMethod("data:" + data + " | deeplink:" + deeplink + " | eventData:" + eventData);
    }

    @JavascriptInterface
    public void gameStatisticEvent(String name, String data) {
        manager.sendGameStat(name, data);
        logMethod("name:" + name + " | data:" + data);
    }

    @JavascriptInterface
    public void showGoodsWidget(String id, String skus) {
        manager.showGoods(skus, id);
    }

    @JavascriptInterface
    public void emptyLoaded() {
    }


    @JavascriptInterface
    public void share(String id, String data) {
        manager.shareData(id, data);
    }




    @JavascriptInterface
    public void  openFilePicker(String data) {
        manager.openFilePicker(data);
    }

    @JavascriptInterface
    public boolean hasFilePicker() {
        return manager.hasFilePicker();
    }
}
