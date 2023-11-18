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
import com.inappstory.sdk.R;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.reader.BaseReaderScreen;
import com.inappstory.sdk.stories.ui.views.goodswidget.GetGoodsDataCallback;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsItemData;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsWidget;
import com.inappstory.sdk.stories.utils.BackPressHandler;

import java.util.ArrayList;

public class GoodsWidgetFragment extends Fragment implements BackPressHandler {
    GetGoodsDataCallback getGoodsDataCallback;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View fragmentView = inflater.inflate(R.layout.cs_goods_custom, null);
        FrameLayout layout = fragmentView.findViewById(R.id.cs_widget_container);
        Context context = getContext();
        View appearanceView =
                AppearanceManager.getCommonInstance().csCustomGoodsWidget().getWidgetView(context);
        if (appearanceView != null) {
            layout.addView(appearanceView);
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
            final GoodsRecyclerView goodsRecyclerView = createRecyclerView(context);
            getGoodsDataCallback = new GetGoodsDataCallback() {
                @Override
                public void onSuccess(ArrayList<GoodsItemData> data) {
                    ProfilingManager.getInstance().setReady(
                            getArguments().getString("localTaskId")
                    );
                    goodsRecyclerView.onSuccess(data);

                }

                @Override
                public void onError() {
                    ProfilingManager.getInstance().setReady(
                            getArguments().getString("localTaskId")
                    );
                    goodsRecyclerView.onError();
                }

                @Override
                public void onClose() {
                    hideGoods();
                }

                @Override
                public void itemClick(String sku) {

                }
            };
            layout.addView(goodsRecyclerView);
        }
        return fragmentView;
    }

    private void hideGoods() {
        BaseReaderScreen screen = ScreensManager.getInstance().currentStoriesReaderScreen;
        if (screen != null) screen.timerIsUnlocked();
        getParentFragmentManager().popBackStack();
    }

    private GoodsRecyclerView createRecyclerView(Context context) {

        final GoodsRecyclerView widgetView = new GoodsRecyclerView(context);
        widgetView.setConfig(new GoodsWidget.GoodsWidgetConfig(
                getArguments().getString("widgetId"),
                (SlideData) getArguments().getSerializable("slideData")
        ));
        widgetView.setOnHideGoodsClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        widgetView.setOnRefreshClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return widgetView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ArrayList<String> skus = JsonParser.listFromJson(
                getArguments().getString("skusString"),
                String.class
        );
        ProfilingManager.getInstance().addTask(
                "goods_resources",
                getArguments().getString("localTaskId")
        );
        AppearanceManager.getCommonInstance().csCustomGoodsWidget().getSkus(skus, getGoodsDataCallback);
    }

    @Override
    public boolean onBackPressed() {
        hideGoods();
        return true;
    }
}
