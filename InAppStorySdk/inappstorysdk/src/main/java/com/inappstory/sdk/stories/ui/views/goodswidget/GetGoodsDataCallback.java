package com.inappstory.sdk.stories.ui.views.goodswidget;

import java.util.ArrayList;

public interface GetGoodsDataCallback {
    void onSuccess(ArrayList<GoodsItemData> data);
    void onError();
    void onClose();
    void itemClick(String sku);
}
