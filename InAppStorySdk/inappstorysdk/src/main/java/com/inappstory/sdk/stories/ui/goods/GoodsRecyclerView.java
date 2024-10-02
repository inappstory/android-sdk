package com.inappstory.sdk.stories.ui.goods;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.ui.views.goodswidget.GetGoodsDataCallback;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsItemData;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsWidget;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsWidgetAppearanceAdapter;
import com.inappstory.sdk.stories.ui.views.goodswidget.ICustomGoodsWidget;
import com.inappstory.sdk.stories.ui.views.goodswidget.IGoodsWidgetAppearance;

import java.util.ArrayList;

public class GoodsRecyclerView extends FrameLayout implements GetGoodsDataCallback {
    private GoodsWidget goodsList;
    private FrameLayout loaderContainer;
    private ImageView refresh;
    private View bottomLine;
    private AppCompatImageView hideGoods;
    private View closeArea;


    public GoodsRecyclerView(Context context, ICustomGoodsWidget customGoodsWidget) {
        super(context);
        initView(context, customGoodsWidget);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView(Context context, ICustomGoodsWidget customGoodsWidget) {
        inflate(context, R.layout.cs_goods_recycler, this);
        goodsList = findViewById(R.id.goods_list);
        goodsList.setParentView(this);
        goodsList.setCustomGoodsWidget(customGoodsWidget);
        loaderContainer = findViewById(R.id.loader_container);
        bottomLine = findViewById(R.id.bottom_line);
        refresh = findViewById(R.id.refresh_button);
        IGoodsWidgetAppearance iGoodsWidgetAppearance = customGoodsWidget.getWidgetAppearance();
        if (iGoodsWidgetAppearance == null) {
            iGoodsWidgetAppearance = new GoodsWidgetAppearanceAdapter();
        }
        if (iGoodsWidgetAppearance instanceof GoodsWidgetAppearanceAdapter) {
            ((GoodsWidgetAppearanceAdapter) iGoodsWidgetAppearance).context = context;
        }
        View closeButtonBackground = findViewById(R.id.hide_goods_container);
        bottomLine.setBackgroundColor(iGoodsWidgetAppearance.getBackgroundColor());
        closeButtonBackground.setBackgroundColor(iGoodsWidgetAppearance.getBackgroundColor());
        bottomLine.getLayoutParams().height = iGoodsWidgetAppearance.getBackgroundHeight();
        bottomLine.requestLayout();
        refresh.setImageDrawable(context.getResources().getDrawable(
                AppearanceManager.getCommonInstance().csRefreshIcon())
        );
        closeArea = findViewById(R.id.close_area);
        closeArea.setBackgroundColor(iGoodsWidgetAppearance.getDimColor());
        hideGoods = findViewById(R.id.hide_goods);
        hideGoods.setImageDrawable(iGoodsWidgetAppearance.getCloseButtonImage());
        hideGoods.setColorFilter(
                new PorterDuffColorFilter(iGoodsWidgetAppearance.getCloseButtonColor(),
                        PorterDuff.Mode.SRC_ATOP)
        );
        loaderContainer.addView(AppearanceManager.getLoader(context));
        loaderContainer.setVisibility(View.VISIBLE);
    }

    void setConfig(GoodsWidget.GoodsWidgetConfig config) {
        goodsList.setConfig(config);
    }

    void setOnHideGoodsClickListener(OnClickListener clickListener) {
        hideGoods.setOnClickListener(clickListener);
        closeArea.setOnClickListener(clickListener);
    }

    void setOnRefreshClickListener(final OnClickListener clickListener) {
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh.setVisibility(View.GONE);
                loaderContainer.setVisibility(View.VISIBLE);
                clickListener.onClick(view);
            }
        });
    }


    @Override
    public void onSuccess(ArrayList<GoodsItemData> data) {
        bottomLine.setVisibility(View.VISIBLE);
        loaderContainer.setVisibility(View.GONE);
        if (data == null || data.isEmpty()) return;
        if (goodsList != null)
            goodsList.setItems(data, this);
    }

    @Override
    public void onError() {
        loaderContainer.setVisibility(View.GONE);
        refresh.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClose() {

    }

    @Override
    public void itemClick(String sku) {

    }
}
