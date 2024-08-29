package com.inappstory.sdk.core.ui.screens;

import static java.util.UUID.randomUUID;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.screens.gamereader.BaseGameScreen;
import com.inappstory.sdk.core.ui.screens.outsideapi.CloseUgcReaderCallback;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEventObserver;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.InAppMessageReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.GetBaseReaderScreenCallback;
import com.inappstory.sdk.stories.ui.goods.GoodsWidgetFragment;
import com.inappstory.sdk.stories.ui.reader.ActiveStoryItem;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;

import java.util.HashMap;

public class ScreensManager {

    private ScreensManager() {

    }

    private final static ScreensManager INSTANCE = new ScreensManager();

    public static ScreensManager getInstance() {
        return INSTANCE;
    }


    public void subscribeGameScreen(BaseGameScreen screen) {
        synchronized (gameReaderScreenLock) {
            if (currentGameScreen != null && currentGameScreen != screen) {
                currentGameScreen.forceFinish();
            }
            currentGameScreen = screen;
        }
    }

    public void resumeStoriesReader() {
        synchronized (storiesReaderScreenLock) {
            if (currentStoriesReaderScreen != null)
                currentStoriesReaderScreen.resumeScreen();
        }
    }

    public void pauseStoriesReader() {
        synchronized (storiesReaderScreenLock) {
            if (currentStoriesReaderScreen != null)
                currentStoriesReaderScreen.pauseScreen();
        }
    }

    public void unsubscribeGameScreen(BaseGameScreen screen) {
        synchronized (gameReaderScreenLock) {
            if (screen == currentGameScreen) {
                currentGameScreen = null;
            }
        }
    }


    private BaseStoryScreen currentStoriesReaderScreen;
    private final Object storiesReaderScreenLock = new Object();


    public void unsubscribeStoryReaderScreen(BaseStoryScreen readerScreen) {
        synchronized (storiesReaderScreenLock) {
            if (currentStoriesReaderScreen == readerScreen) {
                currentStoriesReaderScreen = null;
            }
        }
    }

    public BaseStoryScreen getCurrentStoriesReaderScreen() {
        synchronized (storiesReaderScreenLock) {
            return currentStoriesReaderScreen;
        }
    }

    public void useCurrentStoriesReaderScreen(GetBaseReaderScreenCallback callback) {
        BaseStoryScreen readerScreen = getCurrentStoriesReaderScreen();
        if (readerScreen != null) callback.get(readerScreen);
    }


    private final Object gameReaderScreenLock = new Object();

    public void closeStoryReader(final int action) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                BaseStoryScreen readerScreen = getCurrentStoriesReaderScreen();
                if (readerScreen != null)
                    readerScreen.closeWithAction(action);
            }
        });
    }

    public void forceCloseStoryReader(final ForceCloseReaderCallback callback) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                BaseStoryScreen readerScreen = getCurrentStoriesReaderScreen();
                if (readerScreen != null) {
                    readerScreen.forceFinish();
                }
                if (callback != null)
                    callback.onComplete();
            }
        });
    }

    public boolean isStoryReaderOpened() {
        return getCurrentStoriesReaderScreen() != null;
    }

    public boolean isGameReaderOpened() {
        synchronized (gameReaderScreenLock) {
            return currentGameScreen != null;
        }
    }

    private BaseGameScreen currentGameScreen;

    public ActiveStoryItem activeStoryItem = null;
    public StoryItemCoordinates coordinates = null;

    public void clearCoordinates() {
        coordinates = null;
        activeStoryItem = null;
    }


    public CloseUgcReaderCallback ugcCloseCallback;

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

    public void openGameReader(final Context context,
                               final GameStoryData data,
                               final String gameId,
                               final String observableId,
                               final boolean openedFromStoriesReader) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        synchronized (gameReaderScreenLock) {
            if (currentGameScreen != null) {
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


    public void openInAppMessageReader(
            final Context outerContext,
            final AppearanceManager appearanceManager,
            final InAppMessageReaderLaunchData launchData
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
            final BaseStoryScreen readerScreen,
            final String widgetId,
            final SlideData slideData
    ) {
        final String localTaskId;
        if (widgetId != null) localTaskId = widgetId;
        else localTaskId = randomUUID().toString();
        final FragmentManager fragmentManager = readerScreen.getScreenFragmentManager();
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
