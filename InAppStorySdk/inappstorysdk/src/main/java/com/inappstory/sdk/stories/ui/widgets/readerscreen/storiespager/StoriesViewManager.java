package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.game.reader.GameReaderLoadProgressBar;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.slidestructure.SlideStructure;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.dialog.ContactDialog;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.generated.SimpleStoriesGeneratedView;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.webview.SimpleStoriesWebView;
import com.inappstory.sdk.stories.utils.AudioModes;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.WebPageConvertCallback;
import com.inappstory.sdk.stories.utils.WebPageConverter;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoriesViewManager {
    public int index = -1;

    public int loadedIndex = -1;
    public int loadedId = -1;

    public void setIndex(int index) {
        this.index = index;
    }

    public void swipeUp() {
        if (storiesView != null)
            storiesView.swipeUp();
    }

    public void sendStoryWidgetEvent(String name, String data, String eventData) {
        if (data != null)
            StatisticManager.getInstance().sendStoryWidgetEvent(name, data,
                    pageManager != null ? pageManager.getFeedId() : null);
        if (eventData != null)
            pageManager.widgetEvent(name, eventData);
    }

    void screenshotShare() {
        if (storiesView != null)
            storiesView.screenshotShare();


    }

    void goodsWidgetComplete(String widgetId) {

        if (storiesView != null)
            storiesView.goodsWidgetComplete(widgetId);
    }


    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    void gameComplete(String data) {
        if (storiesView instanceof SimpleStoriesWebView) {
            ((SimpleStoriesWebView) storiesView).gameComplete(data);
        }
    }

    public void sendApiRequest(String data) {
        new JsApiClient(storiesView.getContext()).sendApiRequest(data, new JsApiResponseCallback() {
            @Override
            public void onJsApiResponse(String result, String cb) {
                storiesView.loadJsApiResponse(result, cb);
            }
        });
    }

    public void changeSoundStatus() {
        storiesView.changeSoundStatus();
    }

    public int storyId;
    int slideInCache = 0;

    public void setPageManager(ReaderPageManager pageManager) {
        this.pageManager = pageManager;
    }

    public ReaderPageManager getPageManager() {
        return pageManager;
    }

    ReaderPageManager pageManager;

    public float getClickCoordinate() {
        return storiesView.getCoordinate();//storiesWebView.coordinate1;
    }


    public static final Pattern FONT_SRC = Pattern.compile("@font-face [^}]*src: url\\(['\"](http[^'\"]*)['\"]\\)");


    public void storyLoaded(int oId, int oInd, boolean alreadyLoaded) {
        this.index = oInd;
        loadedIndex = oInd;
        loadedId = oId;
        if (alreadyLoaded) return;
        Story story = InAppStoryService.getInstance() != null ?
                InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, pageManager.getStoryType()) : null;
        innerLoad(story);
    }

    public void storyLoaded(int oId, int oInd) {
        storyLoaded(oId, oInd, false);
    }

    public StoriesViewManager() {
    }

    Context context;

    public StoriesViewManager(Context context) {
        this.context = context;
    }

    void innerLoad(Story story) {
        try {
            setWebViewSettings(story);
        } catch (IOException e) {
            InAppStoryService.createExceptionLog(e);
        }
    }

    public class ShowLoader implements Runnable {
        int slideIndex = -1;

        public ShowLoader(int slideIndex) {
            this.slideIndex = slideIndex;
        }

        @Override
        public void run() {
            clearShowLoader();
            if (this.slideIndex == index)
                pageManager.showLoader(index);
            if (storiesView != null) storiesView.clearSlide(getLatestVisibleIndex());
            setLatestVisibleIndex(-1);
        }
    }


    public class ShowRefresh implements Runnable {
        int slideIndex = -1;

        public ShowRefresh(int slideIndex) {
            this.slideIndex = slideIndex;
        }

        @Override
        public void run() {
            clearShowLoader();
            clearShowRefresh();
            if (this.slideIndex == index)
                pageManager.slideLoadError(index);
            if (storiesView != null) storiesView.clearSlide(getLatestVisibleIndex());
            setLatestVisibleIndex(-1);
        }
    }



    private void clearShowRefresh() {
        synchronized (latestIndexLock) {
            if (showRefresh != null) {
                try {
                    showRefreshHandler.removeCallbacks(showRefresh);
                } catch (Exception e) {}
            }
            showRefresh = null;
        }
    }

    private void clearShowLoader() {
        synchronized (latestIndexLock) {
            if (showLoader != null) {
                try {
                    showRefreshHandler.removeCallbacks(showLoader);
                } catch (Exception e) {}
            }
            showLoader = null;
        }
    }

    private int latestVisibleIndex = -1;
    ShowRefresh showRefresh;
    ShowLoader showLoader;

    Handler showRefreshHandler = new Handler(Looper.getMainLooper());

    public void loadWebData(String layout, String webdata) {

        if (!(storiesView instanceof SimpleStoriesWebView)) return;
        clearShowLoader();
        clearShowRefresh();
        synchronized (latestIndexLock) {
            showRefresh = new ShowRefresh(index);
            showLoader = new ShowLoader(index);
            showRefreshHandler.postDelayed(showLoader, 500);
            showRefreshHandler.postDelayed(showRefresh, 5000);
            Log.e("showRefreshHandler", "create showLoader");
            Log.e("showRefreshHandler", "create showRefresh");
        }
        ((SimpleStoriesWebView) storiesView).loadWebData(layout, webdata);
    }


    boolean lastLoadIsEmpty = false;

    public void loadEmptyData(String layout, String webdata) {
        if (!(storiesView instanceof SimpleStoriesWebView)) return;
        ((SimpleStoriesWebView) storiesView).loadWebData(layout, webdata);
        lastLoadIsEmpty = true;
    }

    private void initViews(SlideStructure slideStructure) {

        ((SimpleStoriesGeneratedView) storiesView).initViews(slideStructure);
    }


    boolean notFirstLoading = false;

    public void loadStory(final int id, final int index) {
        if (InAppStoryService.isNull())
            return;
        final Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(id, pageManager.getStoryType());
        synchronized (this) {
            if (loadedId == id && loadedIndex == index) return;
            if (story == null || story.checkIfEmpty()) {
                return;
            }
            if (story.getSlidesCount() <= index) return;
            storyId = id;
            this.index = index;
            loadedIndex = index;
            loadedId = id;
        }
        slideInCache = InAppStoryService.getInstance().getDownloadManager().checkIfPageLoaded(id, index,
                pageManager.getStoryType());
        if (slideInCache == 1) {
            innerLoad(story);
            pageManager.slideLoadedInCache(index, true);
        } else {

            if (slideInCache == -1) {
                pageManager.slideLoadError(index);
            } else {
                if (!InAppStoryService.isConnected()) {
                    if (CallbackManager.getInstance().getErrorCallback() != null) {
                        CallbackManager.getInstance().getErrorCallback().noConnection();
                    }
                    return;
                }
                pageManager.storyLoadStart();
            }
        }
    }

    void setWebViewSettingsAndLoadEmpty(Story story) {
        if (1 == 1) return;
        String layout = story.getLayout();
        if (storiesView == null || !(storiesView instanceof SimpleStoriesWebView)) return;
        WebPageConvertCallback callback = new WebPageConvertCallback() {
            @Override
            public void onConvert(String webData, String webLayout, int lastIndex) {
                if (index != lastIndex) return;
                loadEmptyData(webLayout, webData);
            }
        };
        ((SimpleStoriesWebView) storiesView).setLayerType(View.LAYER_TYPE_HARDWARE, null);
        converter.replaceEmptyAndLoad(index, layout, callback);
    }

    void setWebViewSettings(Story story) throws IOException {
        String innerWebData = story.pages.get(index);
        String layout = story.getLayout();//getLayoutWithFonts(story.getLayout());
        if (storiesView == null || !(storiesView instanceof SimpleStoriesWebView)) return;

        WebPageConvertCallback callback = new WebPageConvertCallback() {
            @Override
            public void onConvert(String webData, String webLayout, int lastIndex) {
                if (index != lastIndex) return;
                loadWebData(webLayout, webData);
            }
        };
        ((SimpleStoriesWebView) storiesView).setLayerType(View.LAYER_TYPE_HARDWARE, null);
        if (innerWebData.contains("<video")) {
            isVideo = true;
        } else {
            isVideo = false;
        }
        converter.replaceDataAndLoad(innerWebData, story, index, layout, callback);
    }

    WebPageConverter converter = new WebPageConverter();

    String getLayoutWithFonts(String layout) {
        List<String> fonturls = new ArrayList<>();

        Matcher urlMatcher = FONT_SRC.matcher(layout);
        while (urlMatcher.find()) {
            if (urlMatcher.groupCount() == 1) {
                fonturls.add(converter.fromHtml(urlMatcher.group(1)).toString());
            }
        }
        for (String fonturl : fonturls) {
            String fileLink = Downloader.getFontFile(fonturl);
            if (fileLink != null)
                layout = layout.replaceFirst(fonturl, "file://" + fileLink);
        }
        return layout;
    }


    boolean isVideo = false;

    void loadStoryInner(final int id, final int index, Story story) {

    }

    public void setStoriesView(SimpleStoriesView storiesWebView) {
        this.storiesView = storiesWebView;
        storiesWebView.checkIfClientIsSet();
    }


    public GameReaderLoadProgressBar getProgressBar() {
        return progressBar;
    }

    public File getCachedFile(String url, String key) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        LruDiskCache cache = service.getCommonCache();
        try {
            return Downloader.updateFile(cache.getFullFile(key), url, cache, key);
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            return null;
        }
    }

    private GameReaderLoadProgressBar progressBar;

    public void setProgressBar(GameReaderLoadProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    SimpleStoriesView storiesView;

    public void storyShowTextInput(String id, String data) {
        ContactDialog alert = new ContactDialog(storyId, id, data,
                new ContactDialog.SendListener() {
                    @Override
                    public void onSend(final String id, final String data) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                storiesView.sendDialog(id, data);
                            }
                        });
                    }
                },
                new ContactDialog.CancelListener() {
                    @Override
                    public void onCancel(final String id) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                storiesView.cancelDialog(id);
                            }
                        });
                    }
                });
        alert.showDialog((Activity) storiesView.getContext());
    }

    public void storyClick(String payload) {
        if (payload == null || payload.isEmpty() || payload.equals("test")) {
            pageManager.storyClick(null, (int) getClickCoordinate(), false);
        } else if (payload.equals("forbidden")) {
            pageManager.storyClick(null, (int) getClickCoordinate(), true);
        } else {
            //pageManager.showGoods();
            pageManager.storyClick(payload, (int) getClickCoordinate(), false);
        }
    }


    public void shareComplete(String stId, boolean success) {
        storiesView.shareComplete(stId, success);
    }


    public void pageFinished() {
    }

    public void share(String id, String data) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.isShareProcess())
            return;
        service.isShareProcess(true);
        InnerShareData shareData = JsonParser.fromJson(data, InnerShareData.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            ScreensManager.getInstance().setTempShareId(id);
            ScreensManager.getInstance().setTempShareStoryId(storyId);
        } else {
            ScreensManager.getInstance().setOldTempShareId(id);
            ScreensManager.getInstance().setOldTempShareStoryId(storyId);
        }
        Story story = InAppStoryService.getInstance() != null ?
                InAppStoryService.getInstance().getDownloadManager()
                        .getStoryById(storyId, pageManager.getStoryType()) : null;
        if (story != null && shareData != null) {
            shareData.payload = story.getSlideEventPayload(index);
            pageManager.parentManager.showShareView(
                    shareData, storyId, index
            );
        } else {
            service.isShareProcess(false);
        }

    }

    public void storyStartedEvent() {
        if (InAppStoryService.isNotNull())
            pageManager.startStoryTimers();
        ProfilingManager.getInstance().setReady(storyId + "_" + index);
        logMemory();
    }


    private void logMemory() {
        final Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;
        Log.e("memoryUsage", usedMemInMB + " " + availHeapSizeInMB + " " + maxHeapSizeInMB);
    }

    public void storyResumedEvent(double startTime) {
        if (InAppStoryService.isNull()) return;
    }

    public void setAudioManagerMode(String mode) {
        if (context == null) return;
        AudioManager audioManager = (AudioManager)
                context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioModes.getModeVal(mode));
    }

    public void storyShowNext() {
        pageManager.nextStory(ShowStory.ACTION_CUSTOM);
    }

    public void storyShowPrev() {
        pageManager.prevStory(ShowStory.ACTION_CUSTOM);
    }

    public void openGameReaderFromGameCenter(String gameId) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null && context != null) {
            service.openGameReaderWithGC(context, getGameStoryData(), gameId);
        }
    }

    private GameStoryData getGameStoryData() {
        GameStoryData data = null;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null && service.getDownloadManager() != null) {
            Story.StoryType type = pageManager != null ? pageManager.getStoryType() : Story.StoryType.COMMON;
            Story story = service.getDownloadManager().getStoryById(storyId, type);
            if (story != null) {
                data = new GameStoryData(
                        story.id,
                        story.lastIndex,
                        story.slidesCount,
                        story.statTitle,
                        story.tags,
                        pageManager != null ? pageManager.getFeedId() : null,
                        type
                );
            }
        }
        return data;
    }

    public void openGameReaderWithoutGameCenter(String gameUrl, String splashScreenPath, String gameConfig, String resources, String options) {
        ProfilingManager.getInstance().addTask("game_init", "game_" + storyId + "_" + index);
        ScreensManager.getInstance().openGameReader(
                context,
                getGameStoryData(),
                null,
                gameUrl,
                splashScreenPath,
                gameConfig,
                resources,
                options
        );
    }

    private boolean storyIsLoaded = false;

    public void storyLoaded(int slideIndex) {
        if (InAppStoryService.isNull()) return;

        clearShowLoader();
        clearShowRefresh();
        storyIsLoaded = true;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, pageManager.getStoryType());
        if ((slideIndex >= 0 && story.lastIndex != slideIndex)) {
            stopStory();
        } else if (InAppStoryService.getInstance().getCurrentId() != storyId) {
            stopStory();
            setLatestVisibleIndex(slideIndex);
        } else {
            setLatestVisibleIndex(slideIndex);
            pageManager.currentSlideIsLoaded = true;
            playStory();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    resumeStory();
                }
            }, 200);
        }
        pageManager.host.storyLoadedSuccess();


    }


    private void sendShowStoryEvents() {
        if (pageManager != null)
            pageManager.sendShowStoryEvents(storyId);
    }

    public void sendShowSlideEvents() {
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, pageManager.getStoryType());
        if (story != null) {
            ShowSlideCallback showSlideCallback = CallbackManager.getInstance().getShowSlideCallback();
            if (showSlideCallback != null) {
                showSlideCallback.showSlide(story.id, StringsUtils.getNonNull(story.statTitle),
                        StringsUtils.getNonNull(story.tags), story.getSlidesCount(), index,
                        story.getSlideEventPayload(index));
            }
        }
    }

    public void pauseUI() {
        if (pageManager != null)
            pageManager.pauseSlide(false);

    }

    public void resumeUI() {
        if (pageManager != null)
            pageManager.resumeSlide(false);
    }

    public void freezeUI() {
        storiesView.freezeUI();
    }

    public void storySetLocalData(String data, boolean sendToServer) {
        KeyValueStorage.saveString("story" + storyId
                + "__" + InAppStoryService.getInstance().getUserId(), data);

        if (!InAppStoryService.getInstance().getSendStatistic()) return;
        if (sendToServer) {
            NetworkClient.getApi().sendStoryData(Integer.toString(storyId), data, Session.getInstance().id)
                    .enqueue(new NetworkCallback<Response>() {
                        @Override
                        public void onSuccess(Response response) {

                        }

                        @Override
                        public Type getType() {
                            return null;
                        }
                    });
        }
    }

    public int source = 0;

    public void storySendData(String data) {
        if (!InAppStoryService.getInstance().getSendStatistic()) return;
        NetworkClient.getApi().sendStoryData(Integer.toString(storyId), data, Session.getInstance().id)
                .enqueue(new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {

                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                });
    }

    public void stopStory() {
        storiesView.stopVideo();
    }

    public void restartStory() {
        storiesView.restartVideo();
    }

    public void playStory() {
        if (storyIsLoaded) {
            sendShowStoryEvents();
            sendShowSlideEvents();
            storiesView.slideStart();
        }
    }

    public void pauseByClick() {

    }

    public void pauseStory() {
        storiesView.slidePause();
    }

    public void resumeStory() {
        storiesView.resumeVideo();
    }

    public void changeIndex(int index) {
        pageManager.openSlideByIndex(index);
    }

    public void slideLoadError(int index) {
        clearShowRefresh();
        clearShowLoader();
        setLatestVisibleIndex(-1);
        pageManager.slideLoadError(index);
    }

    private final Object latestIndexLock = new Object();

    private void setLatestVisibleIndex(int index) {
        synchronized (latestIndexLock) {
            latestVisibleIndex = index;
        }
    }

    private int getLatestVisibleIndex() {
        synchronized (latestIndexLock) {
            return latestVisibleIndex;
        }
    }
    public void showSingleStory(int storyId, int slideIndex) {
        pageManager.showSingleStory(storyId, slideIndex);
    }

    public void restartStoryWithDuration(long duration) {
        pageManager.restartCurrentWithDuration(duration);
    }

    public void resetTimers() {
        pageManager.resetCurrentDuration();
    }
}
