package com.inappstory.sdk.game.reader;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.utils.KeyValueStorage;


public class GameJSInterface {
    Context mContext;
    GameManager manager;


    /**
     * Instantiate the interface and set the context
     */
    GameJSInterface(Context c, GameManager gameManager) {

        mContext = c;
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
    public void gameInstanceSetLocalData(String gameInstanceId, String data, boolean sendToServer) {
        manager.gameInstanceSetData(gameInstanceId, data, sendToServer);
    }

    @JavascriptInterface
    public String gameInstanceGetLocalData(String gameInstanceId) {
        if (InAppStoryService.isNull()) return "";
        String id = gameInstanceId;
        if (id == null) id = manager.gameCenterId;
        if (id == null) return "";
        String res = KeyValueStorage.getString("gameInstance_" + id
                + "__" + InAppStoryService.getInstance().getUserId());
        return res == null ? "" : res;
    }

    @JavascriptInterface
    public String storyGetLocalData(int storyId) {
        if (InAppStoryService.isNull()) return "";
        String res = KeyValueStorage.getString("story" + storyId
                + "__" + InAppStoryService.getInstance().getUserId());
        return res == null ? "" : res;
    }

    @JavascriptInterface
    public void gameLoaded(String data) {
        manager.gameLoaded(data);
    }

    @JavascriptInterface
    public void sendApiRequest(String data) {
        manager.sendApiRequest(data);
    }

    @JavascriptInterface
    public void gameComplete(String data) {
        manager.gameCompleted(data, null, null);
    }

    @JavascriptInterface
    public void setAudioManagerMode(String mode) {
        manager.setAudioManagerMode(mode);
    }

    @JavascriptInterface
    public void gameComplete(String data, String eventData, String deeplink) {
        manager.gameCompleted(data, deeplink, eventData);
    }

    @JavascriptInterface
    public void gameStatisticEvent(String name, String data) {
        manager.sendGameStat(name, data);
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
}
