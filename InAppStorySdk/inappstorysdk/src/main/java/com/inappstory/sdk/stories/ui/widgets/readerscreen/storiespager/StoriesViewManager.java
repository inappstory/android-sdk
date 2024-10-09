package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenData;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenStrategy;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.api.models.UpdateTimelineData;
import com.inappstory.sdk.stories.ui.widgets.LoadProgressBar;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.ui.dialog.CancelListener;
import com.inappstory.sdk.stories.ui.dialog.ContactDialogCreator;
import com.inappstory.sdk.stories.ui.dialog.SendListener;
import com.inappstory.sdk.stories.ui.dialog.ShowListener;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.stories.ui.reader.StoriesContentFragment;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.webview.SimpleStoriesWebView;
import com.inappstory.sdk.stories.utils.AudioModes;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.WebPageConvertCallback;
import com.inappstory.sdk.stories.utils.WebPageConverter;
import com.inappstory.sdk.utils.ISessionHolder;

import java.io.IOException;
import java.lang.reflect.Type;
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
            core.statistic().v2().sendStoryWidgetEvent(name, data,
                    pageManager != null ? pageManager.getFeedId() : null);
        if (eventData != null)
            pageManager.widgetEvent(name, eventData);
    }

    void screenshotShare(String shareId) {
        if (storiesView != null)
            storiesView.screenshotShare(shareId);
    }

    public void screenshotShareCallback(String shareId) {
        pageManager.screenshotShareCallback(shareId);
    }

    public void vibrate(int[] vibratePattern) {
        if (context != null) {
            core.vibrateUtils().vibrate(context, vibratePattern);
        }
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
        new JsApiClient(
                core,
                storiesView.getActivityContext(),
                ApiSettings.getInstance().getHost()
        ).sendApiRequest(data, new JsApiResponseCallback() {
            @Override
            public void onJsApiResponse(String result, String cb) {
                storiesView.loadJsApiResponse(result, cb);
            }
        });
    }

    public void changeSoundStatus() {
        storiesView.changeSoundStatus(core);
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
        Story story = core
                .contentLoader()
                .storyDownloadManager()
                .getStoryById(storyId, pageManager.getStoryType());
        innerLoad(story);
    }

    Context context;

    public IASCore core() {
        return core;
    }

    private final IASCore core;

    public StoriesViewManager(Context context, IASCore core) {
        this.context = context;
        this.core = core;
    }

    void innerLoad(Story story) {
        try {
            setWebViewSettings(story);
        } catch (IOException e) {
        }
    }

    public class ShowLoader implements Runnable {
        int slideIndex = -1;
        boolean clearSlide = true;
        boolean showBackground = true;

        public ShowLoader(int slideIndex) {
            this.slideIndex = slideIndex;
        }

        public ShowLoader(int slideIndex, boolean clearSlide, boolean showBackground) {
            this.slideIndex = slideIndex;
            this.clearSlide = clearSlide;
            this.showBackground = showBackground;
        }

        @Override
        public void run() {
            clearShowLoader();
            if (this.slideIndex == index) {
                pageManager.showLoader(showBackground);
                if (clearSlide) {
                    if (storiesView != null) storiesView.clearSlide(getLatestVisibleIndex());
                    setLatestVisibleIndex(-1);
                }
            }
        }
    }

    private void clearShowRefresh() {
        synchronized (latestIndexLock) {
            if (showRefresh != null) {
                try {
                    showRefreshHandler.removeCallbacks(showRefresh);
                } catch (Exception e) {
                }
            }
            showRefresh = null;
        }
    }

    private void clearShowLoader() {
        synchronized (latestIndexLock) {
            if (showLoader != null) {
                try {
                    showRefreshHandler.removeCallbacks(showLoader);
                } catch (Exception e) {
                }
            }
            showLoader = null;
        }
    }

    private int latestVisibleIndex = -1;

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
            showRefreshHandler.postDelayed(showRefresh, 15000);
        }
        ((SimpleStoriesWebView) storiesView).loadWebData(layout, webdata);
    }

    public void loadStory(Story story, int index) {
        synchronized (this) {
            if (story == null || story.checkIfEmpty()) {
                return;
            }
            if (loadedId == story.id && loadedIndex == index)
                return;
            if (story.getSlidesCount() <= index) return;
            storyId = story.id;
            this.index = index;
            loadedIndex = index;
            loadedId = storyId;
        }
        slideInCache = core.contentLoader().storyDownloadManager().checkIfPageLoaded(
                storyId,
                index,
                pageManager.getStoryType());
        if (slideInCache == 1) {
            slideInCache(story, index);
        } else {
            slideNotInCache(index);
        }
    }

    private void slideInCache(final Story story, final int index) {
        ISessionHolder sessionHolder = core.sessionManager().getSession();
        if (sessionHolder.checkIfSessionAssetsIsReadySync()) {
            innerLoad(story);
            pageManager.slideLoadedInCache(index, true);
        } else {
            slideNotInCache(index);
        }
    }

    private void slideNotInCache(int index) {
        if (slideInCache == -1) {
            pageManager.slideLoadError(index);
        } else {
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    new ConnectionCheck().check(
                            context,
                            new ConnectionCheckCallback(core) {
                                @Override
                                public void success() {
                                    pageManager.storyLoadStart();
                                }
                            }
                    );
                }
            });

        }
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

        Log.e("JS_method_call", "setWebViewSettings " + story.id + " " + index);
        converter.replaceDataAndLoad(innerWebData, story, index, layout, callback);
    }

    WebPageConverter converter = new WebPageConverter();


    boolean isVideo = false;


    public void setStoriesView(SimpleStoriesView storiesWebView) {
        this.storiesView = storiesWebView;
        storiesWebView.checkIfClientIsSet();
    }


    public LoadProgressBar getProgressBar() {
        return progressBar;
    }


    private LoadProgressBar progressBar;

    public void setProgressBar(LoadProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    SimpleStoriesView storiesView;

    public void storyShowTextInput(String id, String data) {
        final StoriesContentFragment storiesContentFragment =
                (StoriesContentFragment) pageManager.host.getParentFragment();
        final BaseStoryScreen readerScreen =
                storiesContentFragment != null ? storiesContentFragment.getStoriesReader() : null;
        if (readerScreen != null) {
            ContactDialogCreator contactDialogCreator = new ContactDialogCreator(storyId, id, data,
                    new ShowListener() {
                        @Override
                        public void onShow() {
                            readerScreen.timerIsLocked();
                            pageManager.getParentManager().pauseCurrent(false);
                        }
                    },
                    new SendListener() {
                        @Override
                        public void onSend(final String id, final String data) {
                            readerScreen.timerIsUnlocked();
                            pageManager.getParentManager().resumeCurrent(false);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    storiesView.sendDialog(id, data);
                                }
                            });
                        }
                    },
                    new CancelListener() {
                        @Override
                        public void onCancel(final String id) {
                            readerScreen.timerIsUnlocked();
                            pageManager.getParentManager().resumeCurrent(false);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    storiesView.cancelDialog(id);
                                }
                            });
                        }
                    });

            contactDialogCreator.showDialog(
                    readerScreen
            );
        }
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
        ShareProcessHandler shareProcessHandler = core.screensManager().getShareProcessHandler();
        if (shareProcessHandler == null || shareProcessHandler.isShareProcess())
            return;
        shareProcessHandler.isShareProcess(true);
        InnerShareData shareData = JsonParser.fromJson(data, InnerShareData.class);
        shareProcessHandler.shareCompleteListener(
                new IShareCompleteListener(id, storyId) {
                    @Override
                    public void complete(String shareId, boolean shared) {
                        shareComplete(shareId, shared);
                    }
                }
        );
        Story story = core.contentLoader().storyDownloadManager()
                .getStoryById(storyId, pageManager.getStoryType());
        if (story != null && shareData != null) {
            shareData.payload = story.getSlideEventPayload(index);
            pageManager.getParentManager().showShareView(
                    shareData, storyId, index
            );
        } else {
            shareProcessHandler.isShareProcess(false);
        }

    }

    public void storyStartedEvent() {
        //pageManager.startStoryTimers();

        final StoriesContentFragment storiesContentFragment =
                (StoriesContentFragment) pageManager.host.getParentFragment();
        if (storiesContentFragment != null)
            storiesContentFragment.disableDrag(storyId, pageManager.getStoryType());
        core.statistic().profiling().setReady(storyId + "_" + index);
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

    Vibrator vibrator;


    public void openGameReaderFromGameCenter(String gameId) {
        final StoriesContentFragment storiesContentFragment =
                (StoriesContentFragment) pageManager.host.getParentFragment();
        String uniqueId = null;
        if (storiesContentFragment != null) {
            uniqueId = storiesContentFragment.getReaderUniqueId();
            storiesContentFragment.observeGameReader();
        }
        Context context = this.context;
        if (context != null)
            core.screensManager().openScreen(
                    context,
                    new LaunchGameScreenStrategy(core, true)
                            .data(new LaunchGameScreenData(
                                    uniqueId,
                                    getGameStoryData(),
                                    gameId
                            ))
            );

    }


    private GameStoryData getGameStoryData() {
        GameStoryData data = null;
        Story.StoryType type = pageManager != null ? pageManager.getStoryType() : Story.StoryType.COMMON;
        Story story = core.contentLoader().storyDownloadManager().getStoryById(storyId, type);
        if (story != null) {
            data = new GameStoryData(
                    pageManager.getSlideData(story)
            );
        }
        return data;
    }

    private boolean storyIsLoaded = false;

    public void storyLoaded(int slideIndex) {
        clearShowLoader();
        clearShowRefresh();
        storyIsLoaded = true;
        Story story = core
                .contentLoader()
                .storyDownloadManager()
                .getStoryById(
                        storyId,
                        pageManager.getStoryType()
                );
        if ((slideIndex >= 0 && story.lastIndex != slideIndex)) {
            stopStory();
        } else if (core
                .screensManager()
                .getStoryScreenHolder()
                .currentOpenedStoryId() != storyId) {
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
        final Story story = core
                .contentLoader()
                .storyDownloadManager()
                .getStoryById(storyId, pageManager.getStoryType());
        if (story != null) {
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.callbacksAPI().useCallback(
                            IASCallbackType.SHOW_SLIDE,
                            new UseIASCallback<ShowSlideCallback>() {
                                @Override
                                public void use(@NonNull ShowSlideCallback callback) {
                                    callback.showSlide(pageManager.getSlideData(story));
                                }
                            }
                    );
                }
            });
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
        core.keyValueStorage().saveString("story" + storyId + "__" +
                ((IASDataSettingsHolder) core.settingsAPI()).userId(), data);
        if (core.statistic().v1().disabled()) return;
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return;
        }
        if (sendToServer) {
            networkClient.enqueue(
                    networkClient.getApi().sendStoryData(
                            Integer.toString(storyId),
                            data,
                            core.sessionManager().getSession().getSessionId()
                    ),
                    new NetworkCallback<Response>() {
                        @Override
                        public void onSuccess(Response response) {

                        }

                        @Override
                        public Type getType() {
                            return null;
                        }
                    }
            );
        }
    }

    public SourceType source = SourceType.SINGLE;

    public void storySendData(String data) {
        if (core.statistic().v1().disabled()) return;
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return;
        }
        networkClient.enqueue(
                networkClient.getApi().sendStoryData(
                        Integer.toString(storyId),
                        data,
                        core.sessionManager().getSession().getSessionId()
                ),
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {

                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                }
        );
    }

    public void stopStory() {
        storiesView.stopSlide();
    }

    public void restartStory() {
        storiesView.restartSlide(core);
    }

    public void playStory() {
        if (storyIsLoaded) {
            sendShowStoryEvents();
            sendShowSlideEvents();
            storiesView.startSlide(core);
        }
    }

    public void pauseStory() {
        storiesView.pauseSlide();
    }

    public void resumeStory() {
        storiesView.resumeSlide();
    }

    public void changeIndex(int index) {
        Log.e("changeIndex", index + "");
        pageManager.openSlideByIndex(index);
    }

    public void slideLoadError(int index) {
        clearShowRefresh();
        clearShowLoader();
        setLatestVisibleIndex(-1);
        pageManager.slideLoadError(index);
    }

    public void updateTimeline(final UpdateTimelineData data) {
        if (data.showError) {
            slideLoadError(data.slideIndex);
            getPageManager().clearSlideTimerFromJS();
        } else if (data.showLoader) {
            synchronized (latestIndexLock) {
                showLoader = new ShowLoader(index, false, false);
                showRefreshHandler.post(showLoader);
            }
        } else {
            clearShowLoader();
            pageManager.host.storyLoadedSuccess();
        }
        if (data.action.equals("start")) {
            getPageManager().startSlideTimerFromJS(data.duration, data.currentTime, data.slideIndex);
        } else if (data.action.equals("pause")) {
            getPageManager().pauseSlideTimerFromJS();
        } else if (data.action.equals("stop")) {
            getPageManager().pauseSlideTimerFromJS();
        }
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

}
