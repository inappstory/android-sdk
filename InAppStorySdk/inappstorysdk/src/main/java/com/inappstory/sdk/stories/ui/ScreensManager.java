package com.inappstory.sdk.stories.ui;

import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_OVERSCROLL;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static com.inappstory.sdk.AppearanceManager.CS_DISLIKE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_FAVORITE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_FAVORITE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_LIKE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_SHARE;
import static com.inappstory.sdk.AppearanceManager.CS_LIKE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_NAVBAR_COLOR;
import static com.inappstory.sdk.AppearanceManager.CS_REFRESH_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_SHARE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_SOUND_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT_ENABLE;
import static com.inappstory.sdk.game.reader.GameActivity.GAME_READER_REQUEST;
import static java.util.UUID.randomUUID;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.game.reader.GameActivity;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.StartGame;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.reader.BaseReaderScreen;
import com.inappstory.sdk.stories.ui.reader.StoriesActivity;
import com.inappstory.sdk.stories.ui.reader.StoriesDialogFragment;
import com.inappstory.sdk.stories.ui.reader.StoriesFixedActivity;
import com.inappstory.sdk.stories.ui.views.goodswidget.GetGoodsDataCallback;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsItemData;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsWidget;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsWidgetAppearanceAdapter;
import com.inappstory.sdk.stories.ui.views.goodswidget.IGoodsWidgetAppearance;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.HashMap;

public class ScreensManager {

    private ScreensManager() {

    }

    private static volatile ScreensManager INSTANCE;

    public static ScreensManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ScreensManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new ScreensManager();
            }
        }
        return INSTANCE;
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

    public void setTempShareId(String tempShareId) {
        this.tempShareId = tempShareId;
    }

    public void setTempShareStoryId(int tempShareStoryId) {
        this.tempShareStoryId = tempShareStoryId;
    }


    public BaseReaderScreen currentScreen;

    public void closeStoryReader(int action) {
        if (currentScreen != null)
            currentScreen.closeStoryReader(action);
    }

    public void clearCurrentFragment(StoriesDialogFragment fragment) {
        if (currentScreen == fragment)
            currentScreen = null;
    }

    public void clearCurrentActivity(AppCompatActivity activity) {
        if (activity == currentScreen)
            currentScreen = null;
    }

    public GameActivity currentGameActivity;

    int tempShareStoryId;

    String tempShareId;

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


    public void openGameReader(Context context, int storyId, int index, String feedId, String gameUrl, String preloadPath, String gameConfig, String resources) {
        if (InAppStoryService.isNull()) {
            return;
        }
        Intent intent2 = new Intent(context, GameActivity.class);
        intent2.putExtra("gameUrl", gameUrl);

        intent2.putExtra("storyId", Integer.toString(storyId));
        intent2.putExtra("slideIndex", index);
        intent2.putExtra("feedId", feedId);
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        intent2.putExtra("tags", story.tags);
        intent2.putExtra("slidesCount", story.getSlidesCount());
        intent2.putExtra("title", story.title);
        intent2.putExtra("gameConfig", gameConfig);
        intent2.putExtra("gameResources", resources);
        intent2.putExtra("preloadPath", preloadPath != null ? preloadPath : "");
        CsEventBus.getDefault().post(new StartGame(storyId, story.title, story.tags,
                story.getSlidesCount(), index));
        if (CallbackManager.getInstance().getGameCallback() != null) {
            CallbackManager.getInstance().getGameCallback().startGame(storyId, story.title,
                    story.tags, story.getSlidesCount(), index);
        }
        if (Sizes.isTablet()) {
            if (currentScreen != null) {
                String observableUID = randomUUID().toString();
                intent2.putExtra("observableUID", observableUID);
                gameObservables.put(observableUID,
                        new MutableLiveData<GameCompleteEvent>());
                currentScreen.observeGameReader(observableUID);
            }
        } else {
            ((Activity) context).startActivityForResult(intent2, GAME_READER_REQUEST);
        }
    }

    private Long lastOpenTry = -1L;

    public void openStoriesReader(Context outerContext, String listID, AppearanceManager manager,
                                  ArrayList<Integer> storiesIds, int index, int source, Integer slideIndex,
                                  String feed, String feedId) {
        if (System.currentTimeMillis() - lastOpenTry < 1000) {
            return;
        }
        lastOpenTry = System.currentTimeMillis();
        closeGameReader();
        closeUGCEditor();

        if (Sizes.isTablet() && outerContext instanceof AppCompatActivity) {
            closeStoryReader(CloseStory.CUSTOM);
            StoriesDialogFragment storiesDialogFragment = new StoriesDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("index", index);
            bundle.putInt("source", source);
            bundle.putString("feedId", feedId);
            bundle.putInt("slideIndex", slideIndex != null ? slideIndex : 0);
            bundle.putIntegerArrayList("stories_ids", storiesIds);
            if (manager == null) {
                manager = AppearanceManager.getCommonInstance();
            }
            if (manager != null) {
                bundle.putInt(CS_CLOSE_POSITION, manager.csClosePosition());
                bundle.putInt(CS_STORY_READER_ANIMATION, manager.csStoryReaderAnimation());
                bundle.putBoolean(CS_CLOSE_ON_OVERSCROLL, manager.csCloseOnOverscroll());
                bundle.putBoolean(CS_CLOSE_ON_SWIPE, manager.csCloseOnSwipe());
                bundle.putBoolean(CS_HAS_LIKE, manager.csHasLike());
                bundle.putBoolean(CS_HAS_FAVORITE, manager.csHasFavorite());
                bundle.putBoolean(CS_HAS_SHARE, manager.csHasShare());
                bundle.putInt(CS_CLOSE_ICON, manager.csCloseIcon());
                bundle.putInt(CS_REFRESH_ICON, manager.csRefreshIcon());
                bundle.putInt(CS_SOUND_ICON, manager.csSoundIcon());
                bundle.putInt(CS_FAVORITE_ICON, manager.csFavoriteIcon());
                bundle.putInt(CS_LIKE_ICON, manager.csLikeIcon());
                bundle.putInt(CS_DISLIKE_ICON, manager.csDislikeIcon());
                bundle.putInt(CS_SHARE_ICON, manager.csShareIcon());
                bundle.putBoolean(CS_TIMER_GRADIENT_ENABLE, manager.csTimerGradientEnable());
                if (manager.csTimerGradient() != null) {
                    try {
                        bundle.putSerializable(CS_TIMER_GRADIENT, manager.csTimerGradient());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            storiesDialogFragment.setArguments(bundle);
            if (currentScreen != null) {
                currentScreen.forceFinish();
            }
            try {
                storiesDialogFragment.show(
                        ((AppCompatActivity) outerContext).getSupportFragmentManager(),
                        "DialogFragment");
                ScreensManager.getInstance().currentScreen = storiesDialogFragment;
            } catch (IllegalStateException e) {
                InAppStoryService.createExceptionLog(e);

            }
        } else {
            if (currentScreen != null) {
                currentScreen.forceFinish();
            }
            Context ctx = (InAppStoryService.isNotNull() ?
                    InAppStoryService.getInstance().getContext() : outerContext);
            Intent intent2 = new Intent(ctx,
                    (manager != null ? manager.csIsDraggable()
                            : AppearanceManager.getCommonInstance().csIsDraggable()) ?
                            StoriesActivity.class : StoriesFixedActivity.class);
            intent2.putExtra("index", index);
            intent2.putExtra("source", source);
            if (listID != null)
                intent2.putExtra("listID", listID);
            if (feedId != null)
                intent2.putExtra("feedId", feedId);
            intent2.putIntegerArrayListExtra("stories_ids", storiesIds);
            intent2.putExtra("slideIndex", slideIndex);
            if (manager != null) {
                int nightModeFlags =
                        ctx.getResources().getConfiguration().uiMode &
                                Configuration.UI_MODE_NIGHT_MASK;
                intent2.putExtra(CS_CLOSE_POSITION, manager.csClosePosition());
                intent2.putExtra(CS_STORY_READER_ANIMATION, manager.csStoryReaderAnimation());
                intent2.putExtra(CS_CLOSE_ON_OVERSCROLL, manager.csCloseOnOverscroll());
                intent2.putExtra(CS_CLOSE_ON_SWIPE, manager.csCloseOnSwipe());
                intent2.putExtra(CS_NAVBAR_COLOR, nightModeFlags == Configuration.UI_MODE_NIGHT_YES ?
                        manager.csNightNavBarColor() : manager.csNavBarColor());
                intent2.putExtra(CS_HAS_LIKE, manager.csHasLike());
                intent2.putExtra(CS_HAS_FAVORITE, manager.csHasFavorite());
                intent2.putExtra(CS_HAS_SHARE, manager.csHasShare());
                intent2.putExtra(CS_CLOSE_ICON, manager.csCloseIcon());
                intent2.putExtra(CS_REFRESH_ICON, manager.csRefreshIcon());
                intent2.putExtra(CS_SOUND_ICON, manager.csSoundIcon());
                intent2.putExtra(CS_FAVORITE_ICON, manager.csFavoriteIcon());
                intent2.putExtra(CS_LIKE_ICON, manager.csLikeIcon());
                intent2.putExtra(CS_DISLIKE_ICON, manager.csDislikeIcon());
                intent2.putExtra(CS_SHARE_ICON, manager.csShareIcon());
                intent2.putExtra(CS_TIMER_GRADIENT_ENABLE, manager.csTimerGradientEnable());
                intent2.putExtra(CS_TIMER_GRADIENT, manager.csTimerGradient());
            }
            if (outerContext == null) {
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent2);
            } else {
                outerContext.startActivity(intent2);
            }
        }
    }

    public void openStoriesReader(Context outerContext, String listID, AppearanceManager manager,
                                  ArrayList<Integer> storiesIds, int index, int source, String feed, String feedId) {
        openStoriesReader(outerContext, listID, manager, storiesIds, index, source, 0, feed, feedId);
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
        if (AppearanceManager.getCommonInstance().csCustomGoodsWidget().getWidgetView() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.GoodsDialog);
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

            if (StatisticManager.getInstance() != null) {
                StatisticManager.getInstance().sendGoodsOpen(storyId,
                        slideIndex, widgetId, feedId);
            }
            ((RelativeLayout) goodsDialog.findViewById(R.id.cs_widget_container))
                    .addView(AppearanceManager.getCommonInstance()
                            .csCustomGoodsWidget().getWidgetView());
            AppearanceManager.getCommonInstance().csCustomGoodsWidget().getSkus(skus,
                    new GetGoodsDataCallback() {
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
                            if (StatisticManager.getInstance() != null) {
                                StatisticManager.getInstance().sendGoodsClick(storyId,
                                        slideIndex, widgetId, sku, feedId);
                            }
                        }
                    });
        } else {
            AlertDialog.Builder builder = (Sizes.isTablet() && !fullScreen) ? new AlertDialog.Builder(activity) :
                    new AlertDialog.Builder(activity, R.style.GoodsDialog);
            dialogView = inflater.inflate(R.layout.cs_goods_recycler, null);
            builder.setView(dialogView);
            goodsDialog = builder.create();
            goodsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            goodsDialog.show();

            if (StatisticManager.getInstance() != null) {
                StatisticManager.getInstance().sendGoodsOpen(storyId,
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
            bottomLine.setBackgroundColor(iGoodsWidgetAppearance.getBackgroundColor());
            bottomLine.getLayoutParams().height = iGoodsWidgetAppearance.getBackgroundHeight();
            bottomLine.requestLayout();
            final View goodsContainer = goodsDialog.findViewById(R.id.goods_container);
            final ImageView refresh = goodsDialog.findViewById(R.id.refresh_button);
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

            //   goodsContainer.animate().translationY(0).setDuration(600).start();
            final GetGoodsDataCallback callback = new GetGoodsDataCallback() {
                @Override
                public void onSuccess(ArrayList<GoodsItemData> data) {
                    ProfilingManager.getInstance().setReady(localTaskId);
                    bottomLine.setVisibility(View.VISIBLE);
                    loaderContainer.setVisibility(View.GONE);
                    if (data == null || data.isEmpty()) return;
                    if (goodsList != null)
                        goodsList.setItems(data);
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
            goodsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    goodsDialog = null;
                    showGoodsCallback.onResume(widgetId);
                }
            });
            ProfilingManager.getInstance().addTask("goods_resources", localTaskId);
            AppearanceManager.getCommonInstance().csCustomGoodsWidget().getSkus(skus,
                    callback);
            AppCompatImageView hideGoods = goodsDialog.findViewById(R.id.hide_goods);
            hideGoods.setImageDrawable(iGoodsWidgetAppearance.getCloseButtonImage());
            hideGoods.setColorFilter(
                    new PorterDuffColorFilter(iGoodsWidgetAppearance.getCloseButtonColor(),
                            PorterDuff.Mode.SRC_ATOP));
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
