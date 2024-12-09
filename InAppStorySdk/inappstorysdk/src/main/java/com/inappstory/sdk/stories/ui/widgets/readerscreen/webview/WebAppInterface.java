package com.inappstory.sdk.stories.ui.widgets.readerscreen.webview;

import static com.inappstory.sdk.utils.DebugUtils.getMethodName;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.StoryLoadedData;
import com.inappstory.sdk.stories.api.models.UpdateTimelineData;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.StoriesViewManager;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

public class WebAppInterface {
    StoriesViewManager manager;

    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(StoriesViewManager manager) {
        this.manager = manager;
    }


    private void logMethod(String payload) {
        InAppStoryManager.showDLog("JS_method_test",
                manager.storyId + " " + getMethodName() + " " + payload);
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void storyClick(String payload) {
        manager.storyClick(payload);
        logMethod(payload);
    }

    @JavascriptInterface
    public void updateTimeline(String data) {
        if (data != null) {
            Log.e("GoodsTimerCheck", "updateTimeline");
            UpdateTimelineData updateTimelineData = JsonParser.fromJson(data, UpdateTimelineData.class);
            manager.updateTimeline(updateTimelineData);
        }
        logMethod(data);
    }

    @JavascriptInterface
    public void storyLoadingFailed(String data) {
        InAppStoryManager.showELog("LoadSlideCheck",
                "slideLoadingFailed " + data);
        if (data != null) {
            StoryLoadedData loadedData = JsonParser.fromJson(data, StoryLoadedData.class);
            manager.slideLoadError(loadedData.index);
        }
        logMethod("");
    }

    @JavascriptInterface
    public void storyShowSlide(int index) {
        if (manager.index != index) {
            manager.changeIndex(index);
        }
        logMethod("" + index);
    }

    @JavascriptInterface
    public void showSingleStory(int id, int index) {
        logMethod("" + id + " " + index);
        if (manager.storyId != id) {
            manager.showSingleStory(id, index);
        } else if (manager.index != index) {
            manager.changeIndexWithStoryNavigation(index);
        }
    }

    @JavascriptInterface
    public void sendApiRequest(String data) {
        manager.sendApiRequest(data);
    }


    @JavascriptInterface
    public void vibrate(int[] vibratePattern) {
        manager.vibrate(vibratePattern);
    }

    @JavascriptInterface
    public void openGame(String gameInstanceId) {
        manager.openGameReaderFromGameCenter(gameInstanceId);
        logMethod(gameInstanceId);
    }

    @JavascriptInterface
    public void setAudioManagerMode(String mode) {
        manager.setAudioManagerMode(mode);
        logMethod(mode);
    }


    @JavascriptInterface
    public void storyShowNext() {
        manager.storyShowNext();
        logMethod("");
    }

    @JavascriptInterface
    public void storyShowPrev() {
        manager.storyShowPrev();
        logMethod("");
    }

    @JavascriptInterface
    public void storyShowNextSlide(long delay) {
        if (delay == 0) {
            manager.changeIndex(manager.index + 1);
        }
        logMethod("" + delay);
    }

    @JavascriptInterface
    public void storyShowNextSlide() {
        manager.changeIndex(manager.index + 1);
        logMethod("");
    }

    @JavascriptInterface
    public void storyShowTextInput(String id, String data) {
        manager.storyShowTextInput(id, data);
        logMethod("");
    }

    @JavascriptInterface
    public void storyStarted() {
        manager.storyStartedEvent();
        manager.pageFinished();
        logMethod("");
    }

    @JavascriptInterface
    public void storyStarted(double startTime) {
        manager.storyStartedEvent();
        manager.pageFinished();
        logMethod("" + startTime);
    }

    @JavascriptInterface
    public void closeStory(String reason) {
        manager.closeStory(reason.toLowerCase());
        logMethod(reason);
    }

    @JavascriptInterface
    public void storyLoaded() {
        InAppStoryManager.showELog("LoadSlideCheck",
                "slideLoaded");
        manager.storyLoaded(-1);
        logMethod("");
    }

    @JavascriptInterface
    public void storyLoaded(String data) {
        if (data != null) {
            int slideIndex = JsonParser.fromJson(data, StoryLoadedData.class).index;
            manager.storyLoaded(slideIndex);
        } else {
            manager.storyLoaded(-1);
        }
        logMethod(data + "");
    }


    @JavascriptInterface
    public void storyStatisticEvent(
            String name,
            String data,
            String eventData
    ) {
        manager.sendStoryWidgetEvent(name, data, eventData);
        logMethod(name + " " + data + " " + eventData);
    }

    @JavascriptInterface
    public void storyStatisticEvent(
            String name,
            String data
    ) {
        manager.sendStoryWidgetEvent(name, data, data);
        logMethod(name + " " + data);
    }

    @JavascriptInterface
    public void emptyLoaded() {
        logMethod("");
    }

    @JavascriptInterface
    public void share(String id, String data) {
        manager.share(id, data);
        logMethod(id + " " + data);
    }

    @JavascriptInterface
    public void storyFreezeUI() {
        manager.freezeUI();
        logMethod("");
    }


    @JavascriptInterface
    public void storyUnfreezeUI() {
        manager.unfreezeUI();
        logMethod("");
    }


    @JavascriptInterface
    public void storySendData(String data) {
        manager.storySendData(data);
        logMethod(data);
    }

    @JavascriptInterface
    public void storySetLocalData(String data, boolean sendToServer) {
        synchronized (manager) {
            manager.storySetLocalData(data, sendToServer);
            logMethod(data + " " + sendToServer);
        }
    }


    @JavascriptInterface
    public String storyGetLocalData() {
        synchronized (manager) {
            String res = KeyValueStorage.getString("story" + manager.storyId
                    + "__" + InAppStoryService.getInstance().getUserId());
            logMethod(res != null ? res : "");
            return res == null ? "" : res;
        }
    }

    @JavascriptInterface
    public void shareSlideScreenshotCb(String shareId, boolean result) {
        manager.screenshotShareCallback(shareId);
        logMethod(shareId + " " + result);
    }


    @JavascriptInterface
    public void defaultTap(String val) {
        logMethod(val);
    }
}