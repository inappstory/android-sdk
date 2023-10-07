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
import static com.inappstory.sdk.AppearanceManager.CS_READER_BACKGROUND_COLOR;
import static com.inappstory.sdk.AppearanceManager.CS_READER_PRESENTATION_STYLE;
import static com.inappstory.sdk.AppearanceManager.CS_READER_RADIUS;
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
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.game.reader.GameActivity;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.reader.BaseReaderScreen;
import com.inappstory.sdk.stories.ui.reader.OverlapFragment;
import com.inappstory.sdk.stories.ui.reader.StoriesActivity;
import com.inappstory.sdk.stories.ui.reader.StoriesDialogFragment;
import com.inappstory.sdk.stories.ui.reader.StoriesFixedActivity;
import com.inappstory.sdk.stories.ui.reader.StoriesGradientObject;
import com.inappstory.sdk.stories.ui.views.goodswidget.GetGoodsDataCallback;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsItemData;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsWidget;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsWidgetAppearanceAdapter;
import com.inappstory.sdk.stories.ui.views.goodswidget.IGoodsWidgetAppearance;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
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

    public BaseReaderScreen currentScreen;
    public OverlapFragmentObserver overlapFragmentObserver;

    public void closeStoryReader(int action) {
        if (currentScreen != null)
            currentScreen.closeStoryReader(action);
    }

    public void clearCurrentFragment(StoriesDialogFragment fragment) {
        if (currentScreen == fragment)
            currentScreen = null;
    }

    public void clearCurrentActivity(FragmentActivity activity) {
        if (activity == currentScreen)
            currentScreen = null;
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
            intent2.putExtra("storyId", Integer.toString(data.slideData.story.id));
            intent2.putExtra("slideIndex", data.slideData.index);
            intent2.putExtra("slidesCount", data.slideData.story.slidesCount);
            intent2.putExtra("feedId", data.slideData.story.feed);
            intent2.putExtra("storyType", Story.nameFromStoryType(data.slideData.story.storyType));
            intent2.putExtra("tags", data.slideData.story.tags);
            intent2.putExtra("title", data.slideData.story.title);
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
            if (currentScreen != null) {
                String observableUID = randomUUID().toString();
                intent2.putExtra("observableUID", observableUID);
                gameObservables.put(observableUID,
                        new MutableLiveData<GameCompleteEvent>());
                currentScreen.observeGameReader(observableUID);
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

    public void openStoriesReader(Context outerContext, String listID, AppearanceManager manager,
                                  ArrayList<Integer> storiesIds, int index, int source, int firstAction, Integer slideIndex,
                                  String feed, String feedId, Story.StoryType type) {

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && outerContext instanceof Activity) {
            if (((Activity) outerContext).isInMultiWindowMode()) {
                Toast.makeText(outerContext, "Unsupported in split mode", Toast.LENGTH_LONG).show();
                //  return;
            }
        }*/
        if (System.currentTimeMillis() - lastOpenTry < 1000) {
            return;
        }
        lastOpenTry = System.currentTimeMillis();
        closeGameReader();
        closeUGCEditor();
        if (Sizes.isTablet() && outerContext instanceof FragmentActivity) {
            closeStoryReader(CloseStory.CUSTOM);
            StoriesDialogFragment storiesDialogFragment = new StoriesDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("index", index);
            bundle.putInt("source", source);
            bundle.putInt("firstAction", firstAction);
            bundle.putString("storiesType", type.name());
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
                bundle.putInt(CS_READER_RADIUS, manager.csReaderRadius(outerContext));
                bundle.putBoolean(CS_TIMER_GRADIENT_ENABLE, manager.csTimerGradientEnable());
                bundle.putInt(CS_READER_BACKGROUND_COLOR, manager.csReaderBackgroundColor());
                if (manager.csTimerGradient() != null) {
                    try {
                        bundle.putSerializable(CS_TIMER_GRADIENT, manager.csTimerGradient());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    StoriesGradientObject defaultGradient = new StoriesGradientObject()
                            .csGradientHeight(Sizes.getScreenSize(outerContext).y);
                    bundle.putSerializable(CS_TIMER_GRADIENT, defaultGradient);
                }
            }
            storiesDialogFragment.setArguments(bundle);
            if (currentScreen != null) {
                currentScreen.forceFinish();
            }
            try {
                storiesDialogFragment.show(
                        ((FragmentActivity) outerContext).getSupportFragmentManager(),
                        "DialogFragment");
                currentScreen = storiesDialogFragment;
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
            intent2.putExtra("firstAction", firstAction);
            intent2.putExtra("storiesType", type.name());
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
                intent2.putExtra(CS_READER_PRESENTATION_STYLE, manager.csStoryReaderPresentationStyle());
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
                intent2.putExtra(CS_READER_RADIUS, manager.csReaderRadius(outerContext));
                intent2.putExtra(CS_READER_BACKGROUND_COLOR, manager.csReaderBackgroundColor());
                if (manager.csTimerGradient() != null) {
                    try {
                        intent2.putExtra(CS_TIMER_GRADIENT, manager.csTimerGradient());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    StoriesGradientObject defaultGradient = new StoriesGradientObject()
                            .csGradientHeight(Sizes.getScreenSize(outerContext).y);
                    intent2.putExtra(CS_TIMER_GRADIENT, defaultGradient);
                }
            }
            if (outerContext == null) {
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent2);
            } else {
                if (!(outerContext instanceof Activity)) {
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                outerContext.startActivity(intent2);
            }
        }
    }

    public void openStoriesReader(Context outerContext,
                                  String listID,
                                  AppearanceManager manager,
                                  ArrayList<Integer> storiesIds,
                                  int index,
                                  int source,
                                  String feed,
                                  String feedId,
                                  Story.StoryType type) {
        openStoriesReader(
                outerContext,
                listID,
                manager,
                storiesIds,
                index,
                source,
                ShowStory.ACTION_OPEN,
                0,
                feed,
                feedId,
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


        showGoodsCallback.onPause();
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView;
        final ArrayList<String> skus = JsonParser.listFromJson(skusString, String.class);

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
                if (StatisticManager.getInstance() != null) {
                    StatisticManager.getInstance().sendGoodsClick(storyId,
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

            if (StatisticManager.getInstance() != null) {
                StatisticManager.getInstance().sendGoodsOpen(storyId,
                        slideIndex, widgetId, feedId);
            }
            ((RelativeLayout) goodsDialog.findViewById(R.id.cs_widget_container))
                    .addView(AppearanceManager.getCommonInstance()
                            .csCustomGoodsWidget().getWidgetView(activity));
            AppearanceManager.getCommonInstance().csCustomGoodsWidget().getSkus(skus, getGoodsDataCallback);
        } else {
            AlertDialog.Builder builder;
            if (Sizes.isTablet() && !fullScreen) {
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
                    showGoodsCallback.onResume(widgetId);
                    goodsDialog = null;
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
