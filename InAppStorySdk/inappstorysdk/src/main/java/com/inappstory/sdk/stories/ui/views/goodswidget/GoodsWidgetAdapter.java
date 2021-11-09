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
                              GoodsWidget.GoodsWidgetConfig config,
                              Context context) {
        if (items != null)
            this.items.addAll(items);
        this.config = config;
    }


    public GoodsWidgetAdapter(Context context) {

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
                            config.slideIndex, config.widgetId, data.sku);
                }
            }
        }
    }

    @NonNull
    @Override
    public GoodsWidgetItem onCreateViewHolder(@NonNull ViewGroup nParent, int viewType) {
        ICustomGoodsItem customGoodsItem = AppearanceManager.getCommonInstance().csCustomGoodsWidget().getItem();
        if (customGoodsItem != null) {
            return new GoodsWidgetItem(customGoodsItem, this);
        } else {
            final ViewGroup parent = nParent;
            return new GoodsWidgetItem(new ICustomGoodsItem() {
                @NonNull
                @Override
                public View getView() {
                    return LayoutInflater.from(parent.getContext()).inflate(R.layout.cs_goods_default_item,
                            parent, false);
                }

                @Override
                public void bindView(View view, GoodsItemData data) {
                    if (data.description != null) {
                        AppCompatTextView desc = view.findViewById(R.id.description);
                        desc.setText(data.description);
                        setTypeface(desc, false, false, false);
                    }
                    if (data.title != null) {
                        AppCompatTextView title = view.findViewById(R.id.title);
                        title.setText(data.title);
                        setTypeface(title, true, false, false);
                    }
                    if (data.price != null) {
                        AppCompatTextView price = view.findViewById(R.id.price);
                        price.setText(data.price);
                        setTypeface(price, true, false, false);
                    }
                    if (data.oldPrice != null) {
                        AppCompatTextView oldPrice = view.findViewById(R.id.oldPrice);
                        oldPrice.setText(data.oldPrice);
                        setTypeface(oldPrice, true, false, false);
                        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    if (data.image != null)
                        if (InAppStoryService.getInstance() != null)
                            ImageLoader.getInstance().displayImage(data.image, -1, (AppCompatImageView)view.findViewById(R.id.image),
                                    InAppStoryService.getInstance().getCommonCache());
                }
            }, this);
        }
    }

    private void setTypeface(AppCompatTextView textView, boolean bold, boolean italic, boolean secondary) {
        Typeface t = AppearanceManager.getCommonInstance().getFont(secondary, bold, italic);
        int boldV = bold ? 1 : 0;
        int italicV = italic ? 2 : 0;
        textView.setTypeface(t != null ? t : textView.getTypeface(), boldV + italicV);
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
