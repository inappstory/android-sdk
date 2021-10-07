package com.inappstory.sdk.stories.ui.views;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public interface ICustomGoodsWidget {
    View getWidgetView();
    ICustomGoodsItem getItem();
    RecyclerView.ItemDecoration getDecoration();
    void getSkus(ArrayList<String> skus, GetGoodsDataCallback callback);
    void onItemClick(GoodsItemData sku);
}
