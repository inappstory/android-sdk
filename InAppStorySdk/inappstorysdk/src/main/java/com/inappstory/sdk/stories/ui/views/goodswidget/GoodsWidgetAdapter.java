package com.inappstory.sdk.stories.ui.views.goodswidget;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.statistic.StatisticManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoodsWidgetAdapter extends RecyclerView.Adapter<GoodsWidgetItem> {
    ArrayList<GoodsItemData> items = new ArrayList<>();
    GoodsWidget.GoodsWidgetConfig config;
    GetGoodsDataCallback callback;
    ICustomGoodsWidget customGoodsWidget;
    private View parentView;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView instanceof GoodsWidget) {
            this.parentView = ((GoodsWidget) recyclerView).parentView;
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.parentView = null;
    }

    public GoodsWidgetAdapter(
            ICustomGoodsWidget customGoodsWidget,
            ArrayList<GoodsItemData> items,
            GoodsWidget.GoodsWidgetConfig config,
            GetGoodsDataCallback callback
    ) {
        this.customGoodsWidget = customGoodsWidget;
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

    public void onItemClick(GoodsItemData data, View view) {
        customGoodsWidget.onItemClick(
                parentView,
                view,
                data,
                callback
        );
        if (data != null) {
            if (config != null && config.slideData != null) {
                StoryWidgetCallback callback = CallbackManager.getInstance().getStoryWidgetCallback();
                if (callback != null) {
                    Map<String, String> widgetData = new HashMap<>();
                    widgetData.put("story_id", "" + config.slideData.story.id);
                    widgetData.put("feed_id", config.slideData.story.feed);
                    widgetData.put("slide_index", "" + config.slideData.index);
                    widgetData.put("widget_id", config.widgetId);
                    widgetData.put("widget_value", data.sku);
                    callback.widgetEvent(config.slideData, "w-goods-click", widgetData);
                }
                if (StatisticManager.getInstance() != null) {
                    StatisticManager.getInstance().sendGoodsClick(
                            config.slideData.story.id,
                            config.slideData.index,
                            config.widgetId,
                            data.sku,
                            config.slideData.story.feed
                    );
                }
            }
        }
    }

    @NonNull
    @Override
    public GoodsWidgetItem onCreateViewHolder(@NonNull final ViewGroup nParent, int viewType) {
        ICustomGoodsItem customGoodsItem = customGoodsWidget
                .getItem();
        if (customGoodsItem != null) {
            return new GoodsWidgetItem(
                    customGoodsItem,
                    this,
                    nParent.getContext()
            );
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
