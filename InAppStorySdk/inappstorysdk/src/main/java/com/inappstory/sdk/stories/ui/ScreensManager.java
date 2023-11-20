package com.inappstory.sdk.stories.ui;

import static com.inappstory.sdk.game.reader.GameActivity.GAME_READER_REQUEST;
import static java.util.UUID.randomUUID;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.game.reader.GameActivity;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.dialog.CancelListener;
import com.inappstory.sdk.stories.ui.goods.GoodsWidgetFragment;
import com.inappstory.sdk.stories.ui.reader.BaseReaderScreen;
import com.inappstory.sdk.stories.ui.reader.OverlapFragment;
import com.inappstory.sdk.stories.ui.reader.StoriesDialogFragment;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;

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

    public void clearShareIds() {
        setTempShareStatus(false);
        setTempShareStoryId(0);
        setTempShareId(null);
        setOldTempShareStoryId(0);
        setOldTempShareId(null);
    }

    public static long created = 0;

    public void setOldTempShareId(String tempShareId) {
        this.oldTempShareId = tempShareId;
    }

    public void setOldTempShareStoryId(int tempShareStoryId) {
        this.oldTempShareStoryId = tempShareStoryId;
    }

    public int getOldTempShareStoryId() {
        return oldTempShareStoryId;
    }

    public String getOldTempShareId() {
        return oldTempShareId;
    }


    public int getTempShareStoryId() {
        return tempShareStoryId;
    }

    public String getTempShareId() {
        return tempShareId;
    }

    public boolean getTempShareStatus() {
        return tempShareStatus;
    }

    public void setTempShareStatus(boolean tempShareStatus) {
        this.tempShareStatus = tempShareStatus;
    }

    public void setTempShareId(String tempShareId) {
        this.tempShareStatus = false;
        this.tempShareId = tempShareId;
    }


    public void setTempShareStoryId(int tempShareStoryId) {
        this.tempShareStoryId = tempShareStoryId;
    }

    public BaseReaderScreen currentStoriesReaderScreen;
    public OverlapFragmentObserver overlapFragmentObserver;

    public void closeStoryReader(int action) {
        if (currentStoriesReaderScreen != null)
            currentStoriesReaderScreen.closeStoryReader(action);
    }

    public void clearCurrentFragment(StoriesDialogFragment fragment) {
        if (currentStoriesReaderScreen == fragment)
            currentStoriesReaderScreen = null;
    }

    public void clearCurrentActivity(FragmentActivity activity) {
        if (activity == currentStoriesReaderScreen)
            currentStoriesReaderScreen = null;
    }

    public GameActivity currentGameActivity;

    int tempShareStoryId;

    String tempShareId;

    boolean tempShareStatus = false;

    int oldTempShareStoryId;

    String oldTempShareId;

    public Point coordinates = null;

    public interface CloseUgcReaderCallback {
        void onClose();
    }

    public CloseUgcReaderCallback ugcCloseCallback;

    public void closeUGCEditor() {
        if (ugcCloseCallback != null) ugcCloseCallback.onClose();
    }

    public void closeGameReader() {
        if (currentGameActivity != null) {
            currentGameActivity.close();
            currentGameActivity = null;
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
            CancelListener cancelListener,
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
            overlapFragment.cancelListener = cancelListener;
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
                               String resources,
                               String options) {
        if (InAppStoryService.isNull()) {
            return;
        }
        Intent intent2 = new Intent(context, GameActivity.class);
        intent2.putExtra("gameUrl", gameUrl);
        if (data != null) {
            intent2.putExtra("slideData", data.slideData);
        }
        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
            CallbackManager.getInstance().getGameReaderCallback().startGame(
                    data, gameId
            );
        }
        intent2.putExtra("options", options);
        intent2.putExtra("gameId", gameId);
        intent2.putExtra("gameConfig", gameConfig);
        intent2.putExtra("gameResources", resources);
        intent2.putExtra("splashImagePath", splashImagePath != null ? splashImagePath : "");

        if (Sizes.isTablet()) {
            if (currentStoriesReaderScreen != null) {
                String observableUID = randomUUID().toString();
                intent2.putExtra("observableUID", observableUID);
                gameObservables.put(observableUID,
                        new MutableLiveData<GameCompleteEvent>());
                currentStoriesReaderScreen.observeGameReader(observableUID);
            }
        }
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent2, GAME_READER_REQUEST);
            ((Activity) context).overridePendingTransition(0, 0);
        } else {
            try {
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
            } catch (Exception e) {
            }
        }

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

    public void hideGoods() {}

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
