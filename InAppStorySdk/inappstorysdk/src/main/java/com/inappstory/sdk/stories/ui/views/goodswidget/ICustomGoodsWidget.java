package com.inappstory.sdk.stories.ui.views.goodswidget;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public interface ICustomGoodsWidget {
    View getWidgetView();
    ICustomGoodsItem getItem();
    IGoodsWidgetAppearance getWidgetAppearance();
    RecyclerView.ItemDecoration getDecoration();
    void getSkus(ArrayList<String> skus, GetGoodsDataCallback callback);
    void onItemClick(GoodsItemData sku);
}
