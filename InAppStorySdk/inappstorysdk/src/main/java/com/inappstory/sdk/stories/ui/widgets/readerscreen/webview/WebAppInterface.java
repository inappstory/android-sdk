package com.inappstory.sdk.stories.ui.widgets.readerscreen.webview;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.events.ChangeIndexEvent;
import com.inappstory.sdk.stories.events.RestartStoryReaderEvent;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.StoriesViewManager;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

public class WebAppInterface {
    StoriesViewManager manager;

    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(Context c, StoriesViewManager manager) {
        //mContext = c;
        this.manager = manager;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void storyClick(String payload) {
        manager.storyClick(payload);
    }


    @JavascriptInterface
    public void storyShowSlide(int index) {
        if (manager.index != index) {
            CsEventBus.getDefault().post(new ChangeIndexEvent(index));
        }
    }


    @JavascriptInterface
    public void openGameReader(String gameUrl, String preloadPath, String gameConfig, String resources) {
        manager.openGameReader(gameUrl, preloadPath, gameConfig, resources);
    }

    @JavascriptInterface
    public void storyShowNextSlide(final long delay) {
        if (delay != 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CsEventBus.getDefault().post(new RestartStoryReaderEvent(manager.storyId, manager.index, delay));
                }
            }, 100);
        } else {
            CsEventBus.getDefault().post(new ChangeIndexEvent(manager.index + 1));
        }
    }

    @JavascriptInterface
    public void storyShowTextInput(String id, String data) {
        manager.storyShowTextInput(id, data);
    }

    @JavascriptInterface
    public void storyStarted() {
        manager.storyStartedEvent();
    }

    @JavascriptInterface
    public void storyLoaded() {
        manager.storyLoaded();
    }

    @JavascriptInterface
    public void storyStatisticEvent(String name, String data) {
        StatisticManager.getInstance().sendWidgetStoryEvent(name, data);
    }

    @JavascriptInterface
    public void emptyLoaded() {
    }

    @JavascriptInterface
    public void share(String id, String data) {
        manager.share(id, data);
    }

    @JavascriptInterface
    public void storyFreezeUI() {
        manager.freezeUI();
    }


    @JavascriptInterface
    public void storySendData(String data) {
        manager.storySendData(data);
    }

    @JavascriptInterface
    public void storySetLocalData(String data, boolean sendToServer) {
        manager.storySetLocalData(data, sendToServer);

    }


    @JavascriptInterface
    public String storyGetLocalData() {
        String res = KeyValueStorage.getString("story" + manager.storyId
                + "__" + InAppStoryManager.getInstance().getUserId());
        return res == null ? "" : res;
    }


    @JavascriptInterface
    public void defaultTap(String val) {


    }
}