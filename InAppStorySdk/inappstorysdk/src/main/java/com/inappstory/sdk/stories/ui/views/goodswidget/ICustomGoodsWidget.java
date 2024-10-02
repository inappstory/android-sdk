package com.inappstory.sdk.stories.ui.views.goodswidget;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public interface ICustomGoodsWidget {
    View getWidgetView(Context context);
    ICustomGoodsItem getItem();
    IGoodsWidgetAppearance getWidgetAppearance();
    RecyclerView.ItemDecoration getDecoration();
    void getSkus(View widgetView, ArrayList<String> skus, GetGoodsDataCallback callback);
    void onItemClick(View widgetView, View goodsItemView, GoodsItemData sku, GetGoodsDataCallback callback);
}
