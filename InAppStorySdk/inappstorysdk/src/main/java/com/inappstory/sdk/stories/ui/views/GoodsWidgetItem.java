package com.inappstory.sdk.stories.ui.views;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;

public class GoodsWidgetItem extends RecyclerView.ViewHolder {
    GoodsItemData data;

    ICustomGoodsItem customGoodsItem;

    public GoodsWidgetItem(ICustomGoodsItem customGoodsItem) {
        super(customGoodsItem.getView());
        this.customGoodsItem = customGoodsItem;
    }

    public void bind(GoodsItemData data) {
        this.data = data;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppearanceManager.getCommonInstance().csCustomGoodsWidget().onItemClick(GoodsWidgetItem.this.data);
            }
        });
        if (customGoodsItem != null)
            customGoodsItem.bindView(itemView, data);
    }
}
