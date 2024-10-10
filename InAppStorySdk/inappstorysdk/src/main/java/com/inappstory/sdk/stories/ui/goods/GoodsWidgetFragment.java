package com.inappstory.sdk.stories.ui.goods;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.statistic.IASStatisticProfilingImpl;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.stories.ui.views.goodswidget.GetGoodsDataCallback;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsItemData;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsWidget;
import com.inappstory.sdk.stories.ui.views.goodswidget.ICustomGoodsWidget;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoodsWidgetFragment extends Fragment implements IASBackPressHandler {
    GetGoodsDataCallback getGoodsDataCallback;

    public BaseStoryScreen getStoriesReader() {
        BaseStoryScreen screen = null;
        if (getActivity() instanceof BaseStoryScreen) {
            screen = (BaseStoryScreen) getActivity();
        } else if (getParentFragment() instanceof BaseStoryScreen) {
            screen = (BaseStoryScreen) getParentFragment();
        }
        return screen;
    }

    ICustomGoodsWidget customGoodsWidget = AppearanceManager.getCommonInstance().csCustomGoodsWidget();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.cs_goods_custom, null);
    }

    public void hideGoods() {
        BaseStoryScreen readerScreen = getStoriesReader();
        if (readerScreen instanceof ShowGoodsCallback) {
            ((ShowGoodsCallback) readerScreen).goodsIsClosed(
                    getArguments().getString("widgetId")
            );
        }
        getParentFragmentManager().popBackStack();
    }

    private GoodsRecyclerView createRecyclerView(Context context) {
        final GoodsRecyclerView widgetView = new GoodsRecyclerView(context, customGoodsWidget);
        widgetView.setConfig(new GoodsWidget.GoodsWidgetConfig(
                getArguments().getString("widgetId"),
                (SlideData) getArguments().getSerializable("slideData")
        ));
        widgetView.setOnHideGoodsClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideGoods();
            }
        });

        widgetView.setOnRefreshClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> skus = JsonParser.listFromJson(
                        getArguments().getString("skusString"),
                        String.class
                );
                customGoodsWidget.getSkus(widgetView, skus, getGoodsDataCallback);
            }
        });
        getGoodsDataCallback = new GetGoodsDataCallback() {
            @Override
            public void onSuccess(ArrayList<GoodsItemData> data) {
                InAppStoryManager.useCore(new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        core.statistic().profiling().setReady(
                                getArguments().getString("localTaskId")
                        );
                    }
                });
                ((GoodsRecyclerView)widgetView).onSuccess(data);

            }

            @Override
            public void onError() {
                InAppStoryManager.useCore(new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        core.statistic().profiling().setReady(
                                getArguments().getString("localTaskId")
                        );
                    }
                });
                ((GoodsRecyclerView)widgetView).onError();
            }

            @Override
            public void onClose() {
                hideGoods();
            }

            @Override
            public void itemClick(final String sku) {
                final SlideData slideData =
                        (SlideData) getArguments().getSerializable("slideData");
                final String widgetId = getArguments().getString("widgetId");
                if (slideData == null) return;
                InAppStoryManager.useCore(new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        core.callbacksAPI().useCallback(IASCallbackType.STORY_WIDGET,
                                new UseIASCallback<StoryWidgetCallback>() {
                                    @Override
                                    public void use(@NonNull StoryWidgetCallback callback) {
                                        Map<String, String> widgetData = new HashMap<>();
                                        widgetData.put("story_id", "" + slideData.story.id);
                                        widgetData.put("feed_id", slideData.story.feed);
                                        widgetData.put("slide_index", "" + slideData.index);
                                        widgetData.put("widget_id", widgetId);
                                        widgetData.put("widget_value", sku);
                                        callback.widgetEvent(slideData, "w-goods-click", widgetData);
                                    }
                                }
                        );
                        core.statistic().v2().sendGoodsClick(slideData.story.id,
                                slideData.index, widgetId, sku, slideData.story.feed);
                    }
                });
            }
        };
        return widgetView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.statistic().profiling().addTask(
                        "goods_resources",
                        getArguments().getString("localTaskId")
                );
            }
        });

        final ArrayList<String> skus = JsonParser.listFromJson(
                getArguments().getString("skusString"),
                String.class
        );
        FrameLayout layout = view.findViewById(R.id.cs_widget_container);
        Context context = getContext();
        View appearanceView = customGoodsWidget.getWidgetView(context);
        if (appearanceView != null) {
            getGoodsDataCallback = new GoodsDataCallbackImpl(
                    (SlideData) getArguments().getSerializable("slideData"),
                    getArguments().getString("widgetId")
            ) {
                @Override
                public void onClose() {
                    hideGoods();
                }
            };
        } else {
            appearanceView = createRecyclerView(context);
        }
        layout.addView(appearanceView);
        AppearanceManager.getCommonInstance().csCustomGoodsWidget().getSkus(
                appearanceView,
                skus,
                getGoodsDataCallback
        );
    }

    @Override
    public boolean onBackPressed() {
        hideGoods();
        return true;
    }
}
