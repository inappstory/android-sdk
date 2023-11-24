package com.inappstory.sdk.stories.ui;

import static java.util.UUID.randomUUID;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.game.reader.BaseGameReaderScreen;
import com.inappstory.sdk.game.reader.GameScreenOptions;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.share.ShareListener;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.goods.GoodsWidgetFragment;
import com.inappstory.sdk.stories.ui.reader.BaseReaderScreen;
import com.inappstory.sdk.stories.ui.reader.OverlapFragment;

import java.util.HashMap;
import java.util.List;

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

    public void clearShareIds() {
        shareCompleteListener(null);
    }

    public static long created = 0;

    public void setTempShareStatus(boolean tempShareStatus) {
        this.tempShareStatus = tempShareStatus;
    }

    public BaseReaderScreen currentStoriesReaderScreen;
    public OverlapFragmentObserver overlapFragmentObserver;

    public void closeStoryReader(int action) {
        if (currentStoriesReaderScreen != null)
            currentStoriesReaderScreen.closeStoryReader(action);
    }


    public BaseGameReaderScreen currentGameScreen;


    public Boolean getTempShareStatus() {
        Boolean status = tempShareStatus;
        tempShareStatus = null;
        return status;
    }

    private Boolean tempShareStatus = null;

    private IShareCompleteListener shareCompleteListener = null;

    private final Object shareListenerLock = new Object();

    public void shareCompleteListener(IShareCompleteListener shareCompleteListener)  {
        synchronized (shareListenerLock) {
            this.shareCompleteListener = shareCompleteListener;
        }
    }

    public IShareCompleteListener shareCompleteListener() {
        synchronized (shareListenerLock) {
            return this.shareCompleteListener;
        }
    }

    public Point coordinates = null;

    public interface CloseUgcReaderCallback {
        void onClose();
    }

    public CloseUgcReaderCallback ugcCloseCallback;

    public void closeUGCEditor() {
        if (ugcCloseCallback != null) ugcCloseCallback.onClose();
    }

    public void closeGameReader() {
        if (currentGameScreen != null) {
            currentGameScreen.forceFinish();
            currentGameScreen = null;
        }
    }

    HashMap<String, MutableLiveData<GameCompleteEvent>> gameObservables = new HashMap<>();

    public MutableLiveData<GameCompleteEvent> getGameObserver(String id) {
        return gameObservables.get(id);
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
            overlapFragment.shareListener = shareListener;
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

    public void openGameReader(Context context,
                               GameStoryData data,
                               String gameId,
                               String gameUrl,
                               String splashImagePath,
                               String gameConfig,
                               List<WebResource> gameResources,
                               GameScreenOptions options,
                               String observableId) {
        if (InAppStoryService.isNull()) {
            return;
        }
        gameObservables.put(observableId,
                new MutableLiveData<GameCompleteEvent>());
        GameReaderLaunchData gameReaderLaunchData = new GameReaderLaunchData(
                gameId,
                observableId,
                gameUrl,
                splashImagePath,
                gameConfig,
                gameResources,
                options,
                data != null ? data.slideData : null
        );
        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
            CallbackManager.getInstance().getGameReaderCallback().startGame(
                    data, gameId
            );
        }
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return;
        Bundle bundle = new Bundle();
        bundle.putSerializable(gameReaderLaunchData.getSerializableKey(), gameReaderLaunchData);
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
                if (currentStoriesReaderScreen != null) {
                    currentStoriesReaderScreen.forceFinish();
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

    public Dialog goodsDialog;

    public void hideGoods() {
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
