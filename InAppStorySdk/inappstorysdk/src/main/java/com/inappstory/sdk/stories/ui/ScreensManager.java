package com.inappstory.sdk.stories.ui;

import static java.util.UUID.randomUUID;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.game.reader.BaseGameReaderScreen;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.share.ShareListener;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEventObserver;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.goods.GoodsWidgetFragment;
import com.inappstory.sdk.stories.ui.reader.ActiveStoryItem;
import com.inappstory.sdk.stories.ui.reader.BaseReaderScreen;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;
import com.inappstory.sdk.stories.ui.reader.OverlapFragment;

import java.util.HashMap;

public class ScreensManager {

    private ScreensManager() {

    }

    private static ScreensManager INSTANCE;
    private static final Object lock = new Object();

    public static ScreensManager getInstance() {
        synchronized (lock) {
            if (INSTANCE == null)
                INSTANCE = new ScreensManager();
            return INSTANCE;
        }
    }


    public void forceCloseAllReaders(final ForceCloseReaderCallback callback) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                forceCloseStoryReader(callback);
                forceFinishGameReader();
                closeUGCEditor();
            }
        });
    }

    public void subscribeGameScreen(BaseGameReaderScreen screen) {
        synchronized (gameReaderScreenLock) {
            if (currentGameScreen != null && currentGameScreen != screen) {
                currentGameScreen.forceFinish();
            }
            currentGameScreen = screen;
        }
        synchronized (gameReaderScreenLock) {
            gameOpenProcessLaunched = false;
        }
    }

    public void resumeStoriesReader() {
        synchronized (storiesReaderScreenLock) {
            if (currentStoriesReaderScreen != null)
                currentStoriesReaderScreen.resumeReader();
        }
    }

    public void pauseStoriesReader() {
        synchronized (storiesReaderScreenLock) {
            if (currentStoriesReaderScreen != null)
                currentStoriesReaderScreen.pauseReader();
        }
    }

    public void unsubscribeGameScreen(BaseGameReaderScreen screen) {
        synchronized (gameReaderScreenLock) {
            if (screen == currentGameScreen) {
                currentGameScreen = null;
            }
        }
    }

    public void setTempShareStatus(boolean tempShareStatus) {
        this.tempShareStatus = tempShareStatus;
    }

    private BaseReaderScreen currentStoriesReaderScreen;

    public void subscribeReaderScreen(BaseReaderScreen readerScreen) {
        synchronized (storiesReaderScreenLock) {
            currentStoriesReaderScreen = readerScreen;
        }
    }

    public void unsubscribeReaderScreen(BaseReaderScreen readerScreen) {
        cleanOverlapFragmentObserver();
        synchronized (storiesReaderScreenLock) {
            if (currentStoriesReaderScreen == readerScreen) {
                currentStoriesReaderScreen = null;
            }
        }
    }

    public BaseReaderScreen getCurrentStoriesReaderScreen() {
        synchronized (storiesReaderScreenLock) {
            return currentStoriesReaderScreen;
        }
    }


    public void useCurrentStoriesReaderScreen(GetBaseReaderScreenCallback callback) {
        BaseReaderScreen readerScreen = getCurrentStoriesReaderScreen();
        if (readerScreen != null) callback.get(readerScreen);
    }


    private final Object storiesReaderScreenLock = new Object();
    private final Object gameReaderScreenLock = new Object();

    public OverlapFragmentObserver overlapFragmentObserver;

    public void closeStoryReader(final int action) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                BaseReaderScreen readerScreen = getCurrentStoriesReaderScreen();
                if (readerScreen != null)
                    readerScreen.closeStoryReader(action);
            }
        });
    }

    @MainThread
    public void forceCloseStoryReader(final ForceCloseReaderCallback callback) {
        BaseReaderScreen readerScreen = getCurrentStoriesReaderScreen();
        if (readerScreen != null) {
            readerScreen.forceFinish();
        }
        if (callback != null)
            callback.onComplete();
    }

    public boolean isStoryReaderOpened() {
        return getCurrentStoriesReaderScreen() != null;
    }

    public boolean isGameReaderOpened() {
        synchronized (gameReaderScreenLock) {
            return currentGameScreen != null;
        }
    }

    private BaseGameReaderScreen currentGameScreen;


    public Boolean getTempShareStatus() {
        Boolean status = tempShareStatus;
        tempShareStatus = null;
        return status;
    }

    private Boolean tempShareStatus = null;

    private IShareCompleteListener shareCompleteListener = null;

    private final Object shareListenerLock = new Object();

    public void shareCompleteListener(IShareCompleteListener shareCompleteListener) {
        synchronized (shareListenerLock) {
            this.shareCompleteListener = shareCompleteListener;
        }
    }

    public IShareCompleteListener shareCompleteListener() {
        synchronized (shareListenerLock) {
            return this.shareCompleteListener;
        }
    }

    public ActiveStoryItem activeStoryItem = null;
    public StoryItemCoordinates coordinates = null;

    public void clearCoordinates() {
        coordinates = null;
        activeStoryItem = null;
    }

    public interface CloseUgcReaderCallback {
        void onClose();
    }

    public CloseUgcReaderCallback ugcCloseCallback;

    @MainThread
    public void closeUGCEditor() {
        if (ugcCloseCallback != null) ugcCloseCallback.onClose();
    }

    public void closeGameReader() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                synchronized (gameReaderScreenLock) {
                    if (currentGameScreen != null) {
                        currentGameScreen.close();
                    }
                }
            }
        });
    }

    @MainThread
    public void forceFinishGameReader() {
        synchronized (gameReaderScreenLock) {
            if (currentGameScreen != null) {
                currentGameScreen.forceFinish();
            }
        }
    }

    HashMap<String, GameCompleteEventObserver> gameObservables = new HashMap<>();

    public GameCompleteEventObserver getGameObserver(String id) {
        return gameObservables.get(id);
    }

    public void putGameObserver(String id, GameCompleteEventObserver observer) {
        gameObservables.put(id, observer);
    }

    public void removeGameObserver(String id) {
        gameObservables.remove(id);
    }

    public void cleanOverlapFragmentObserver() {
        this.overlapFragmentObserver = null;
    }

    public void openOverlapContainerForShare(
            ShareListener shareListener,
            FragmentManager fragmentManager,
            OverlapFragmentObserver observer,
            String slidePayload,
            int storyId,
            int slideIndex,
            IASShareData shareData
    ) {
        try {
            this.overlapFragmentObserver = observer;
            OverlapFragment overlapFragment = new OverlapFragment();
            Bundle bundle = new Bundle();
            bundle.putString("slidePayload", slidePayload);
            bundle.putInt("storyId", storyId);
            bundle.putInt("slideIndex", slideIndex);
            bundle.putSerializable("shareData", shareData);
            overlapFragment.setArguments(bundle);
            overlapFragment.setShareListener(shareListener);
            FragmentTransaction t = fragmentManager.beginTransaction()
                    .replace(R.id.ias_outer_top_container, overlapFragment);
            t.addToBackStack("OverlapFragment");
            t.commit();
        } catch (IllegalStateException e) {
            InAppStoryService.createExceptionLog(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Object gameOpenProcessLock = new Object();
    private boolean gameOpenProcessLaunched = false;

    public void openGameReader(final Context context,
                               final GameStoryData data,
                               final String gameId,
                               final String observableId,
                               final boolean openedFromStoriesReader) {
        synchronized (gameReaderScreenLock) {
            if (gameOpenProcessLaunched) return;
            gameOpenProcessLaunched = true;
        }
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        synchronized (gameReaderScreenLock) {
            if (currentGameScreen != null) {
                InAppStoryManager.showELog("GameReader", "Game reader already opened");
                return;
            }
        }
        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
            CallbackManager.getInstance().getGameReaderCallback().startGame(
                    data, gameId
            );
        }
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return;
        GameReaderLaunchData gameReaderLaunchData = new GameReaderLaunchData(
                gameId,
                observableId,
                data != null ? data.slideData : null
        );
        Bundle bundle = new Bundle();
        /*GameReaderAppearanceSettings gameReaderAppearanceSettings = new GameReaderAppearanceSettings(
                openedFromStoriesReader ? "#303030" : null,
                openedFromStoriesReader ? "#303030" : null
        );*/
        GameReaderAppearanceSettings gameReaderAppearanceSettings = new GameReaderAppearanceSettings(
                openedFromStoriesReader ? "#000000" : null,
                openedFromStoriesReader ? "#000000" : null
        );
        bundle.putSerializable(gameReaderLaunchData.getSerializableKey(), gameReaderLaunchData);
        bundle.putSerializable(gameReaderAppearanceSettings.getSerializableKey(), gameReaderAppearanceSettings);
        inAppStoryManager.getOpenGameReader().onOpen(
                context,
                bundle
        );
    }

    private Long lastOpenTry = -1L;

    public void openStoriesReader(
            final Context outerContext,
            final AppearanceManager appearanceManager,
            final StoriesReaderLaunchData launchData
    ) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.getSession().getSessionId().isEmpty()) return;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
                if (inAppStoryManager == null) return;
                if (System.currentTimeMillis() - lastOpenTry < 1000) {
                    return;
                }
                lastOpenTry = System.currentTimeMillis();
                closeGameReader();
                closeUGCEditor();

                synchronized (storiesReaderScreenLock) {
                    if (currentStoriesReaderScreen != null) {
                        currentStoriesReaderScreen.forceFinish();
                    }
                }
                AppearanceManager manager = appearanceManager;
                if (manager == null) manager = AppearanceManager.getCommonInstance();
                if (manager == null) manager = new AppearanceManager();
                StoriesReaderAppearanceSettings appearanceSettings = new StoriesReaderAppearanceSettings(
                        manager,
                        outerContext
                );
                Bundle bundle = new Bundle();
                bundle.putSerializable(
                        launchData.getSerializableKey(),
                        launchData
                );
                bundle.putSerializable(
                        appearanceSettings.getSerializableKey(),
                        appearanceSettings
                );
                inAppStoryManager.getOpenStoriesReader().onOpen(
                        outerContext,
                        bundle
                );
            }
        });

    }

    public void showGoods(
            String skusString,
            final BaseReaderScreen readerScreen,
            final String widgetId,
            final SlideData slideData
    ) {
        final String localTaskId;
        if (widgetId != null) localTaskId = widgetId;
        else localTaskId = randomUUID().toString();
        final FragmentManager fragmentManager = readerScreen.getStoriesReaderFragmentManager();
        if (StatisticManager.getInstance() != null && slideData != null) {
            StatisticManager.getInstance().sendGoodsOpen(slideData.story.id,
                    slideData.index, widgetId, slideData.story.feed);
        }

        GoodsWidgetFragment fragment = new GoodsWidgetFragment();
        Bundle args = new Bundle();
        args.putString("localTaskId", localTaskId);
        args.putSerializable("slideData", slideData);
        args.putString("widgetId", widgetId);
        args.putString("skusString", skusString);
        fragment.setArguments(args);
        FragmentTransaction t = fragmentManager.beginTransaction()
                .replace(R.id.ias_outer_top_container, fragment);
        t.addToBackStack("GoodsWidgetFragment");
        t.commit();

    }
}
