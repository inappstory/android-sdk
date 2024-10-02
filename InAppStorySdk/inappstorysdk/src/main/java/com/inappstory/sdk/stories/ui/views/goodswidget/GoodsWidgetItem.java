package com.inappstory.sdk.stories.ui.views.goodswidget;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;

public class GoodsWidgetItem extends RecyclerView.ViewHolder {
    GoodsItemData data;
    GoodsWidgetAdapter adapter;
    ICustomGoodsItem customGoodsItem;

    public GoodsWidgetItem(
            ICustomGoodsItem customGoodsItem,
            GoodsWidgetAdapter adapter,
            Context context
    ) {
        super(customGoodsItem.getView(context));
        this.customGoodsItem = customGoodsItem;
        this.adapter = adapter;
    }

    public void bind(GoodsItemData data, final GetGoodsDataCallback callback) {
        this.data = data;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.onItemClick(GoodsWidgetItem.this.data, GoodsWidgetItem.this.itemView);
            }
        });
        if (customGoodsItem != null)
            customGoodsItem.bindView(itemView, data);
    }
}
