package com.inappstory.sdk.stories.ui.views.goodswidget;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;

import com.inappstory.sdk.core.repository.statistic.StatisticV2Manager;

import java.util.ArrayList;

public class GoodsWidgetAdapter extends RecyclerView.Adapter<GoodsWidgetItem> {
    ArrayList<GoodsItemData> items = new ArrayList<>();
    GoodsWidget.GoodsWidgetConfig config;
    GetGoodsDataCallback callback;

    public GoodsWidgetAdapter(ArrayList<GoodsItemData> items,
                              GoodsWidget.GoodsWidgetConfig config,
                              GetGoodsDataCallback callback) {
        this.callback = callback;
        if (items != null)
            this.items.addAll(items);
        this.config = config;
    }


    public void setItems(ArrayList<GoodsItemData> items) {
        if (items != null) {
            this.items.clear();
            this.items.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void onItemClick(GoodsItemData data) {
        if (data != null) {
            if (config != null) {
                if (StatisticV2Manager.getInstance() != null) {
                    StatisticV2Manager.getInstance().sendGoodsClick(config.storyId,
                            config.slideIndex, config.widgetId, data.sku, config.feedId);
                }
            }
        }
    }

    @NonNull
    @Override
    public GoodsWidgetItem onCreateViewHolder(@NonNull final ViewGroup nParent, int viewType) {
        ICustomGoodsItem customGoodsItem = AppearanceManager
                .getCommonInstance()
                .csCustomGoodsWidget()
                .getItem();
        if (customGoodsItem != null) {
            return new GoodsWidgetItem(customGoodsItem, this, nParent.getContext());
        } else {
            return new GoodsWidgetItem(
                    new SimpleCustomGoodsItem(),
                    this,
                    nParent.getContext()
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsWidgetItem holder, int position) {
        holder.bind(this.items.get(position), callback);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
