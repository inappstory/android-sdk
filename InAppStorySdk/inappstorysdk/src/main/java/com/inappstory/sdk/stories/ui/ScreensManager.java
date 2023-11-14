package com.inappstory.sdk.stories.ui;

import static com.inappstory.sdk.game.reader.GameActivity.GAME_READER_REQUEST;
import static java.util.UUID.randomUUID;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.AppearanceManager;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.reader.GameActivity;
import com.inappstory.sdk.game.reader.GameLaunchData;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.core.utils.network.JsonParser;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.core.models.api.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SourceType;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.core.repository.statistic.ProfilingManager;
import com.inappstory.sdk.core.repository.statistic.StatisticV2Manager;
import com.inappstory.sdk.stories.ui.reader.BaseReaderScreen;
import com.inappstory.sdk.stories.ui.reader.OverlapFragment;
import com.inappstory.sdk.stories.ui.reader.StoriesDialogFragment;
import com.inappstory.sdk.stories.ui.views.goodswidget.GetGoodsDataCallback;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsItemData;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsWidget;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsWidgetAppearanceAdapter;
import com.inappstory.sdk.stories.ui.views.goodswidget.IGoodsWidgetAppearance;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
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

    public void closeStoryReader(CloseReader action, String cause) {
        if (currentStoriesReaderScreen != null)
            currentStoriesReaderScreen.closeStoryReader(action, cause);
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
            Context context,
            OverlapFragmentObserver observer,
            String slidePayload,
            int storyId,
            int slideIndex,
            IASShareData shareData
    ) {
        try {
            if (!(context instanceof FragmentActivity)) return;
            this.overlapFragmentObserver = observer;
            OverlapFragment storiesDialogFragment = new OverlapFragment();
            storiesDialogFragment.setCancelable(false);
            Bundle bundle = new Bundle();
            bundle.putString("slidePayload", slidePayload);
            bundle.putInt("storyId", storyId);
            bundle.putInt("slideIndex", slideIndex);
            bundle.putString("shareData", JsonParser.getJson(shareData));
            storiesDialogFragment.setArguments(bundle);
            storiesDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(),
                    "OverlapFragment");
        } catch (IllegalStateException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openGameReader(Context context,
                               GameStoryData gameStoryData,
                               String gameId,
                               GameLaunchData gameLaunchData) {
        Intent intent2 = new Intent(context, GameActivity.class);
        if (gameStoryData != null) {
            intent2.putExtra("storyId", Integer.toString(gameStoryData.slideData.story.id));
            intent2.putExtra("slideIndex", gameStoryData.slideData.index);
            intent2.putExtra("slidesCount", gameStoryData.slideData.story.slidesCount);
            intent2.putExtra("feedId", gameStoryData.slideData.story.feed);
            intent2.putExtra("storyType", Story.nameFromStoryType(gameStoryData.slideData.story.storyType));
            intent2.putExtra("tags", gameStoryData.slideData.story.tags);
            intent2.putExtra("title", gameStoryData.slideData.story.title);
            intent2.putExtra("gameStoryData", gameStoryData);
        }
        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
            CallbackManager.getInstance().getGameReaderCallback().startGame(
                    gameStoryData, gameId
            );
        }
        intent2.putExtra("gameId", gameId);
        intent2.putExtra("gameLaunchData", gameLaunchData);
        if (gameLaunchData != null) {
            intent2.putExtra("options", gameLaunchData.getOptions());
            intent2.putExtra("gameConfig", gameLaunchData.getGameConfig());
            intent2.putExtra("gameResources", gameLaunchData.getResources());
            intent2.putExtra("splashImagePath", gameLaunchData.getSplashImagePath());
            intent2.putExtra("gameUrl", gameLaunchData.getGameUrl());
        }

        if (Sizes.isTablet(context)) {
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
            Context outerContext,
            String listID,
            AppearanceManager manager,
            List<Integer> storiesIds,
            int index,
            SourceType sourceType,
            int firstAction,
            Integer slideIndex,
            String feed,
            Story.StoryType type
    ) {
        if (manager == null) {
            manager = AppearanceManager.getCommonInstance();
        }
        if (manager == null)
            manager = new AppearanceManager();

        if (System.currentTimeMillis() - lastOpenTry < 1000) {
            return;
        }
        lastOpenTry = System.currentTimeMillis();
        closeGameReader();
        closeUGCEditor();
        if (currentStoriesReaderScreen != null) {
            currentStoriesReaderScreen.forceFinish();
        }
        IASCore.getInstance().getOpenStoriesReader().onOpen(
                outerContext,
                new StoriesReaderAppearanceSettings(
                        manager,
                        outerContext
                ),
                new StoriesReaderLaunchData(
                        listID,
                        feed,
                        storiesIds,
                        index,
                        firstAction,
                        sourceType,
                        slideIndex,
                        type
                )
        );
    }

    public void openStoriesReader(
            Context outerContext,
            String listID,
            AppearanceManager manager,
            List<Integer> storiesIds,
            int index,
            SourceType sourceType,
            String feed,
            Story.StoryType type
    ) {
        openStoriesReader(
                outerContext,
                listID,
                manager,
                storiesIds,
                index,
                sourceType,
                ShowStory.ACTION_OPEN,
                0,
                feed,
                type);
    }


    public Dialog goodsDialog;

    public void hideGoods() {
        if (goodsDialog != null) {
            goodsDialog.dismiss();
            goodsDialog = null;
        }
    }

    public void showGoods(String skusString, Activity activity, final ShowGoodsCallback showGoodsCallback,
                          boolean fullScreen, final String widgetId,
                          final int storyId, final int slideIndex, final String feedId) {
        if (AppearanceManager.getCommonInstance().csCustomGoodsWidget() == null) {
            showGoodsCallback.onEmptyResume(widgetId);
            Log.d("InAppStory_SDK_error", "Empty goods widget");
            return;
        }
        if (goodsDialog != null) return;

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView;
        final ArrayList<String> skus = JsonParser.listFromJson(skusString, String.class);
        showGoodsCallback.onPause();

        final String localTaskId;
        if (widgetId != null) localTaskId = widgetId;
        else localTaskId = randomUUID().toString();
        GetGoodsDataCallback getGoodsDataCallback = new GetGoodsDataCallback() {
            @Override
            public void onSuccess(ArrayList<GoodsItemData> data) {
                if (data == null || data.isEmpty()) return;
            }

            @Override
            public void onError() {

            }

            @Override
            public void onClose() {
                hideGoods();
            }

            @Override
            public void itemClick(String sku) {
                if (StatisticV2Manager.getInstance() != null) {
                    StatisticV2Manager.getInstance().sendGoodsClick(storyId,
                            slideIndex, widgetId, sku, feedId);
                }
            }
        };
        if (AppearanceManager.getCommonInstance().csCustomGoodsWidget().getWidgetView(activity) != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.StoriesSDKAppTheme_GoodsDialog);
            dialogView = inflater.inflate(R.layout.cs_goods_custom, null);
            builder.setView(dialogView);
            goodsDialog = builder.create();
            //dialog.setContentView(R.layout.cs_goods_recycler);
            goodsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            goodsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    showGoodsCallback.onResume(widgetId);
                    goodsDialog = null;
                }
            });
            //goodsDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
            goodsDialog.show();

            if (StatisticV2Manager.getInstance() != null) {
                StatisticV2Manager.getInstance().sendGoodsOpen(storyId,
                        slideIndex, widgetId, feedId);
            }
            ((RelativeLayout) goodsDialog.findViewById(R.id.cs_widget_container))
                    .addView(AppearanceManager.getCommonInstance()
                            .csCustomGoodsWidget().getWidgetView(activity));
            AppearanceManager.getCommonInstance().csCustomGoodsWidget().getSkus(skus, getGoodsDataCallback);
        } else {
            AlertDialog.Builder builder;
            if (Sizes.isTablet(activity) && !fullScreen) {
                builder = new AlertDialog.Builder(activity);
            } else {
                builder = new AlertDialog.Builder(activity, R.style.StoriesSDKAppTheme_GoodsDialog);
            }
            dialogView = inflater.inflate(R.layout.cs_goods_recycler, null);
            builder.setView(dialogView);
            goodsDialog = builder.create();
            goodsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            goodsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            goodsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams attrs = goodsDialog.getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            goodsDialog.getWindow().setAttributes(attrs);
            View decorView = goodsDialog.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            goodsDialog.show();
            //  goodsDialog.findViewById(R.id.goods_container).setTranslationY(Sizes.dpToPxExt(240));
            // goodsDialog.findViewById(R.id.goods_container).animate().translationY(0).setDuration(600).start();

            if (StatisticV2Manager.getInstance() != null) {
                StatisticV2Manager.getInstance().sendGoodsOpen(storyId,
                        slideIndex, widgetId, feedId);
            }
            final GoodsWidget goodsList = goodsDialog.findViewById(R.id.goods_list);
            goodsList.setConfig(new GoodsWidget.GoodsWidgetConfig(widgetId, storyId, slideIndex, feedId));
            final FrameLayout loaderContainer = goodsDialog.findViewById(R.id.loader_container);
            IGoodsWidgetAppearance iGoodsWidgetAppearance = AppearanceManager.getCommonInstance().csCustomGoodsWidget().getWidgetAppearance();
            if (iGoodsWidgetAppearance == null) {
                iGoodsWidgetAppearance = new GoodsWidgetAppearanceAdapter();
            }
            if (iGoodsWidgetAppearance instanceof GoodsWidgetAppearanceAdapter) {
                ((GoodsWidgetAppearanceAdapter) iGoodsWidgetAppearance).context = activity;
            }

            final View bottomLine = goodsDialog.findViewById(R.id.bottom_line);
            View closeButtonBackground = goodsDialog.findViewById(R.id.hide_goods_container);
            bottomLine.setBackgroundColor(iGoodsWidgetAppearance.getBackgroundColor());
            closeButtonBackground.setBackgroundColor(iGoodsWidgetAppearance.getBackgroundColor());
            bottomLine.getLayoutParams().height = iGoodsWidgetAppearance.getBackgroundHeight();
            Log.e("goodsWidgetHeight", "" + iGoodsWidgetAppearance.getBackgroundHeight());
            bottomLine.requestLayout();
            final ImageView refresh = goodsDialog.findViewById(R.id.refresh_button);
            refresh.setImageDrawable(activity.getResources().getDrawable(AppearanceManager.getCommonInstance().csRefreshIcon()));

            final GetGoodsDataCallback callback = new GetGoodsDataCallback() {
                @Override
                public void onSuccess(ArrayList<GoodsItemData> data) {
                    ProfilingManager.getInstance().setReady(localTaskId);
                    bottomLine.setVisibility(View.VISIBLE);
                    loaderContainer.setVisibility(View.GONE);
                    if (data == null || data.isEmpty()) return;
                    if (goodsList != null)
                        goodsList.setItems(data, this);
                }

                @Override
                public void onError() {
                    ProfilingManager.getInstance().setReady(localTaskId);
                    loaderContainer.setVisibility(View.GONE);
                    refresh.setVisibility(View.VISIBLE);
                }

                @Override
                public void onClose() {
                    hideGoods();
                }

                @Override
                public void itemClick(String sku) {

                }
            };
            goodsDialog.findViewById(R.id.close_area).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideGoods();
                }
            });
            goodsDialog.findViewById(R.id.close_area)
                    .setBackgroundColor(iGoodsWidgetAppearance.getDimColor());
            loaderContainer.addView(AppearanceManager.getLoader(goodsDialog.getContext()));
            loaderContainer.setVisibility(View.VISIBLE);

            goodsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    goodsDialog = null;
                    showGoodsCallback.onResume(widgetId);
                }
            });
            ProfilingManager.getInstance().addTask("goods_resources", localTaskId);
            AppearanceManager.getCommonInstance().csCustomGoodsWidget().getSkus(skus, callback);
            AppCompatImageView hideGoods = goodsDialog.findViewById(R.id.hide_goods);
            hideGoods.setImageDrawable(iGoodsWidgetAppearance.getCloseButtonImage());
            hideGoods.setColorFilter(
                    new PorterDuffColorFilter(iGoodsWidgetAppearance.getCloseButtonColor(),
                            PorterDuff.Mode.SRC_ATOP)
            );
            hideGoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideGoods();
                }
            });
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refresh.setVisibility(View.GONE);
                    loaderContainer.setVisibility(View.VISIBLE);
                    ProfilingManager.getInstance().addTask("goods_resources", localTaskId);
                    AppearanceManager.getCommonInstance().csCustomGoodsWidget().getSkus(skus,
                            callback);
                }
            });
        }


    }
}
