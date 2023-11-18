package com.inappstory.sdk.stories.ui.goods;

import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.views.goodswidget.GetGoodsDataCallback;
import com.inappstory.sdk.stories.ui.views.goodswidget.GoodsItemData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class GoodsDataCallbackImpl implements GetGoodsDataCallback {
    private SlideData slideData;
    private String widgetId;

    public GoodsDataCallbackImpl(SlideData slideData, String widgetId) {
        this.slideData = slideData;
        this.widgetId = widgetId;
    }

    @Override
    public void onSuccess(ArrayList<GoodsItemData> data) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void itemClick(String sku) {
        if (slideData == null) return;
        StoryWidgetCallback callback = CallbackManager.getInstance().getStoryWidgetCallback();
        if (callback != null) {
            Map<String, String> widgetData = new HashMap<>();
            widgetData.put("story_id", "" + slideData.story.id);
            widgetData.put("feed_id", slideData.story.feed);
            widgetData.put("slide_index", "" + slideData.index);
            widgetData.put("widget_id", widgetId);
            widgetData.put("widget_value", sku);
            callback.widgetEvent(slideData, "w-goods-click", widgetData);
        }
        if (StatisticManager.getInstance() != null) {
            StatisticManager.getInstance().sendGoodsClick(slideData.story.id,
                    slideData.index, widgetId, sku, slideData.story.feed);
        }
    }
}
