package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.View;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.game.reader.GameStoryData;
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
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.dialog.CancelListener;
import com.inappstory.sdk.stories.ui.dialog.ContactDialogCreator;
import com.inappstory.sdk.stories.ui.dialog.SendListener;
import com.inappstory.sdk.stories.ui.dialog.ShowListener;
import com.inappstory.sdk.stories.ui.reader.BaseReaderScreen;
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
            StatisticManager.getInstance().sendStoryWidgetEvent(name, data,
                    pageManager != null ? pageManager.getFeedId() : null);
        if (eventData != null)
            pageManager.widgetEvent(name, eventData);
    }

    void screenshotShare() {
        if (storiesView != null)
            storiesView.screenshotShare();


    }

    public void vibrate(int[] vibratePattern) {
        if (context != null) {
            InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
            if (inAppStoryManager != null) {
                inAppStoryManager.getVibrateUtils().vibrate(context, vibratePattern);
            }
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
                InAppStoryService.getInstance().getStoryDownloadManager().getStoryById(storyId, pageManager.getStoryType()) : null;
        innerLoad(story);
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
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
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
        slideInCache = service.getStoryDownloadManager().checkIfPageLoaded(
                storyId,
                index,
                pageManager.getStoryType());
        if (slideInCache == 1) {
            slideInCache(service, story, index);
        } else {
            slideNotInCache(service, index);
        }
    }

    private void slideInCache(final InAppStoryService service, final Story story, final int index) {

        ISessionHolder sessionHolder = service.getSession();
        if (sessionHolder.checkIfSessionAssetsIsReady()) {
            innerLoad(story);
            pageManager.slideLoadedInCache(index, true);
        } else {
            slideNotInCache(service, index);
        }
    }

    private void slideNotInCache(InAppStoryService service, int index) {
        if (slideInCache == -1) {
            pageManager.slideLoadError(index);
        } else {
            if (!service.isConnected()) {
                if (CallbackManager.getInstance().getErrorCallback() != null) {
                    CallbackManager.getInstance().getErrorCallback().noConnection();
                }
                return;
            }
            pageManager.storyLoadStart();
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
        final BaseReaderScreen readerScreen =
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
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.isShareProcess())
            return;
        service.isShareProcess(true);
        InnerShareData shareData = JsonParser.fromJson(data, InnerShareData.class);
        ScreensManager.getInstance().shareCompleteListener(
                new IShareCompleteListener(id, storyId) {
                    @Override
                    public void complete(String shareId, boolean shared) {
                        shareComplete(shareId, shared);
                    }
                }
        );
        Story story = InAppStoryService.getInstance() != null ?
                InAppStoryService.getInstance().getStoryDownloadManager()
                        .getStoryById(storyId, pageManager.getStoryType()) : null;
        if (story != null && shareData != null) {
            shareData.payload = story.getSlideEventPayload(index);
            pageManager.getParentManager().showShareView(
                    shareData, storyId, index
            );
        } else {
            service.isShareProcess(false);
        }

    }

    public void storyStartedEvent() {
        pageManager.startStoryTimers();

        final StoriesContentFragment storiesContentFragment =
                (StoriesContentFragment) pageManager.host.getParentFragment();
        if (storiesContentFragment != null)
            storiesContentFragment.disableDrag(storyId, pageManager.getStoryType());
        ProfilingManager.getInstance().setReady(storyId + "_" + index);
    }

    public void storyResumedEvent(double startTime) {
        if (pageManager != null)
            pageManager.moveTimerToPosition(startTime);
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
        InAppStoryService service = InAppStoryService.getInstance();
        final StoriesContentFragment storiesContentFragment =
                (StoriesContentFragment) pageManager.host.getParentFragment();
        String uniqueId = null;
        if (storiesContentFragment != null) {
            uniqueId = storiesContentFragment.getReaderUniqueId();
            storiesContentFragment.observeGameReader();
        }
        if (service != null && context != null) {
            service.openGameReaderWithGC(
                    context,
                    getGameStoryData(),
                    gameId,
                    uniqueId
            );
        }
    }


    private GameStoryData getGameStoryData() {
        GameStoryData data = null;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null && service.getStoryDownloadManager() != null) {
            Story.StoryType type = pageManager != null ? pageManager.getStoryType() : Story.StoryType.COMMON;
            Story story = service.getStoryDownloadManager().getStoryById(storyId, type);
            if (story != null) {
                data = new GameStoryData(
                        pageManager.getSlideData(story)
                );
            }
        }
        return data;
    }

    private boolean storyIsLoaded = false;

    public void storyLoaded(int slideIndex) {

        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        clearShowLoader();
        clearShowRefresh();
        storyIsLoaded = true;
        Story story = service.getStoryDownloadManager().getStoryById(storyId, pageManager.getStoryType());
        if ((slideIndex >= 0 && story.lastIndex != slideIndex)) {
            stopStory();
        } else if (service.getCurrentId() != storyId) {
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
        Story story = InAppStoryService.getInstance().getStoryDownloadManager().getStoryById(storyId, pageManager.getStoryType());
        if (story != null) {
            ShowSlideCallback showSlideCallback = CallbackManager.getInstance().getShowSlideCallback();
            if (showSlideCallback != null) {
                showSlideCallback.showSlide(pageManager.getSlideData(story));
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
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        KeyValueStorage.saveString("story" + storyId + "__" + service.getUserId(), data);
        if (service.statV1Disallowed()) return;
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return;
        }
        if (sendToServer) {
            networkClient.enqueue(
                    networkClient.getApi().sendStoryData(
                            Integer.toString(storyId),
                            data,
                            service.getSession().getSessionId()
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
        if (InAppStoryService.getInstance().statV1Disallowed()) return;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (service.statV1Disallowed()) return;
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return;
        }
        networkClient.enqueue(
                networkClient.getApi().sendStoryData(
                        Integer.toString(storyId),
                        data,
                        service.getSession().getSessionId()
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
        storiesView.stopVideo();
    }

    public void restartStory() {
        storiesView.restartVideo();
    }

    public void restartSlide() {
        pageManager.restartCurrentWithoutDuration();
        ((SimpleStoriesWebView) storiesView).reloadPage();
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
