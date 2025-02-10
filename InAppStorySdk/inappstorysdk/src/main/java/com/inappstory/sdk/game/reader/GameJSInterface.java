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
        try {
            return manager.pausePlaybackOtherApp();
        } catch (Exception e) {
            manager.sendSdkError(e);
            return 0;
        }

    }

    @JavascriptInterface
    public void storySetLocalData(int storyId, String data, boolean sendToServer) {
        try {
            manager.storySetData(data, sendToServer);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }
    }

    @JavascriptInterface
    public void openUrl(String data) {
        try {
            manager.openUrl(data);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }

    }

    @JavascriptInterface
    public void vibrate(int[] vibratePattern) {
        try {
            manager.vibrate(vibratePattern);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }
    }

    @JavascriptInterface
    public void gameInstanceSetLocalData(String gameInstanceId, String data, boolean sendToServer) {
        try {

            manager.gameInstanceSetData(gameInstanceId, data, sendToServer);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }
    }

    @JavascriptInterface
    public String gameInstanceGetLocalData(String gameInstanceId) {
        try {
            InAppStoryService service = InAppStoryService.getInstance();
            if (service == null) return "";
            String id = gameInstanceId;
            if (id == null) id = manager.gameCenterId;
            if (id == null) return "";
            String res = KeyValueStorage.getString("gameInstance_" + id
                    + "__" + service.getUserId());
            return res == null ? "" : res;
        } catch (Exception e) {
            manager.sendSdkError(e);
            return "";
        }
    }

    @JavascriptInterface
    public String storyGetLocalData(int storyId) {
        try {
            InAppStoryService service = InAppStoryService.getInstance();
            if (service == null) return "";
            String res = KeyValueStorage.getString("story" + storyId
                    + "__" + service.getUserId());
            return res == null ? "" : res;
        } catch (Exception e) {
            manager.sendSdkError(e);
            return "";
        }
    }

    @JavascriptInterface
    public void event(
            String name,
            String data
    ) {
        try {
            manager.jsEvent(name,  data);
            logMethod("name:" + name + " | data:" + data);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }

    }

    @JavascriptInterface
    public void gameShouldForegroundCallback(String data) {
        try {
            manager.gameShouldForegroundCallback(data);
            logMethod(data);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }

    }


    @JavascriptInterface
    public void gameLoaded() {
        try {
            manager.gameLoaded();
            logMethod("");
        } catch (Exception e) {
            manager.sendSdkError(e);
        }

    }

    private void logMethod(String payload) {
        InAppStoryManager.showDLog("JS_game_method_test",
                manager.gameCenterId + " " + getMethodName() + " " + payload);
    }

    @JavascriptInterface
    public void gameLoadFailed(String reason, boolean canTryReload) {
        try {
            manager.gameLoadFailed(reason, canTryReload);
            logMethod("reason:" + reason + " | canTryReload:" + canTryReload);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }
    }

    @JavascriptInterface
    public void reloadGameReader() {
        try {
            manager.clearTries();
            manager.reloadGame();
            logMethod("null");
        } catch (Exception e) {
            manager.sendSdkError(e);
        }
    }

    @JavascriptInterface
    public void sendApiRequest(String data) {
        try {
            manager.sendApiRequest(data);
            logMethod(data);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }
    }

    @JavascriptInterface
    public void gameComplete(String data) {
        try {
            manager.gameCompleted(data, null, null);
            logMethod(data);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }

    }

    @JavascriptInterface
    public void setAudioManagerMode(String mode) {
        try {
            manager.setAudioManagerMode(mode);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }

    }

    @JavascriptInterface
    public void gameComplete(String data, String eventData, String urlOrOptions) {
        try {
            manager.gameCompleted(data, urlOrOptions, eventData);
            logMethod("data:" + data + " | deeplink:" + urlOrOptions + " | eventData:" + eventData);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }

    }

    @JavascriptInterface
    public void gameStatisticEvent(String name, String data) {
        try {
            manager.sendGameStat(name, data);
            logMethod("name:" + name + " | data:" + data);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }
    }

    @JavascriptInterface
    public void showGoodsWidget(String id, String skus) {
        try {
            manager.showGoods(skus, id);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }

    }

    @JavascriptInterface
    public void emptyLoaded() {
    }


    @JavascriptInterface
    public void share(String id, String data) {
        try {
            manager.shareData(id, data);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }
    }




    @JavascriptInterface
    public void  openFilePicker(String data) {
        try {
            manager.openFilePicker(data);
        } catch (Exception e) {
            manager.sendSdkError(e);
        }

    }

    @JavascriptInterface
    public boolean hasFilePicker() {
        try {
            return manager.hasFilePicker();
        } catch (Exception e) {
            manager.sendSdkError(e);
            return false;
        }
    }
}
