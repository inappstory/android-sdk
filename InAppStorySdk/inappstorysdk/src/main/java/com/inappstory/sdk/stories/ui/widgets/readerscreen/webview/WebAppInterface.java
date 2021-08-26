package com.inappstory.sdk.stories.ui.widgets.readerscreen.webview;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.StoryLoadedData;
import com.inappstory.sdk.stories.events.ChangeIndexEvent;
import com.inappstory.sdk.stories.events.ClearDurationEvent;
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
    public void openGameReader(String gameFile, String coverFile, String initCode, String gameResources) {
        manager.openGameReader(gameFile, coverFile, initCode, gameResources);
    }

    @JavascriptInterface
    public void resetTimers() {
        CsEventBus.getDefault().post(new ClearDurationEvent(manager.storyId, manager.index));
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
    public void storyStarted(double startTime) {
        manager.storyStartedEvent();
    }

    @JavascriptInterface
    public void storyResumed(double startTime) {
        manager.storyResumedEvent(startTime);
    }

    @JavascriptInterface
    public void storyLoaded() {
        manager.storyLoaded(-1);
    }

    @JavascriptInterface
    public void storyLoaded(String data) {
        if (data != null) {
            int slideIndex = JsonParser.fromJson(data, StoryLoadedData.class).index;
            manager.storyLoaded(slideIndex);
        } else {
            manager.storyLoaded(-1);
        }
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
                + "__" + InAppStoryService.getInstance().getUserId());
        return res == null ? "" : res;
    }


    @JavascriptInterface
    public void defaultTap(String val) {


    }
}