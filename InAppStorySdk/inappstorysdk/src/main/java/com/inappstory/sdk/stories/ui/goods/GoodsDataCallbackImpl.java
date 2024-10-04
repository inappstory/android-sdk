package com.inappstory.sdk.stories.ui.goods;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
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
    public void itemClick(final String sku) {
        if (slideData == null) return;

        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().useCallback(IASCallbackType.STORY_WIDGET,
                        new UseIASCallback<StoryWidgetCallback>() {
                            @Override
                            public void use(@NonNull StoryWidgetCallback callback) {
                                Map<String, String> widgetData = new HashMap<>();
                                widgetData.put("story_id", "" + slideData.story.id);
                                widgetData.put("feed_id", slideData.story.feed);
                                widgetData.put("slide_index", "" + slideData.index);
                                widgetData.put("widget_id", widgetId);
                                widgetData.put("widget_value", sku);
                                callback.widgetEvent(slideData, "w-goods-click", widgetData);
                            }
                        }
                );
                core.statistic().v2().sendGoodsClick(slideData.story.id,
                        slideData.index, widgetId, sku, slideData.story.feed);
            }
        });
    }
}
