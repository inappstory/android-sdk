package com.inappstory.sdk.stories.ui.views.goodswidget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;

public class GoodsWidget extends RecyclerView {
    Context context;
    GoodsWidgetAdapter adapter;

    @Nullable
    @Override
    public GoodsWidgetAdapter getAdapter() {
        return adapter;
    }

    public static class GoodsWidgetConfig {
        public String widgetId;
        public int storyId;
        public String feedId;
        public int slideIndex;

        public GoodsWidgetConfig(String widgetId, int storyId, int slideIndex, String feedId) {
            this.widgetId = widgetId;
            this.storyId = storyId;
            this.feedId = feedId;
            this.slideIndex = slideIndex;
        }
    }


    public void setConfig(GoodsWidgetConfig config) {
        this.config = config;
    }

    GoodsWidgetConfig config;
    public void setItems(ArrayList<GoodsItemData> items) {
        adapter = new GoodsWidgetAdapter(items, config, context);
        setAdapter(adapter);
    }

    private void init(Context context) {
        this.context = context;
        setLayoutManager(new LinearLayoutManager(context, HORIZONTAL, false));
        if (AppearanceManager.getCommonInstance().csCustomGoodsWidget().getDecoration() != null) {
            addItemDecoration(AppearanceManager.getCommonInstance().csCustomGoodsWidget().getDecoration());
        } else {
            addItemDecoration(new ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect,
                                           @NonNull View view,
                                           @NonNull RecyclerView parent,
                                           @NonNull RecyclerView.State state) {
                    int position = parent.getChildAdapterPosition(view);
                    outRect.left = Sizes.dpToPxExt(8, getContext());
                    outRect.right = Sizes.dpToPxExt(8, getContext());
                    if (position == 0) {
                        outRect.left = Sizes.dpToPxExt(16, getContext());
                    } else if (position == adapter.getItemCount() - 1) {
                        outRect.right = Sizes.dpToPxExt(16, getContext());
                    }
                }
            });
        }
    }

    public GoodsWidget(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GoodsWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GoodsWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
}
