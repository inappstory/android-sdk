package com.inappstory.sdk.stories.ui.views.goodswidget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.stories.statistic.StatisticManager;

import java.util.ArrayList;

public class GoodsWidgetAdapter extends RecyclerView.Adapter<GoodsWidgetItem> {
    ArrayList<GoodsItemData> items = new ArrayList<>();
    GoodsWidget.GoodsWidgetConfig config;

    public GoodsWidgetAdapter(ArrayList<GoodsItemData> items,
                              GoodsWidget.GoodsWidgetConfig config) {
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
                if (StatisticManager.getInstance() != null) {
                    StatisticManager.getInstance().sendGoodsClick(config.storyId,
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
                .getItem(nParent.getContext());
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
        holder.bind(this.items.get(position));
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
