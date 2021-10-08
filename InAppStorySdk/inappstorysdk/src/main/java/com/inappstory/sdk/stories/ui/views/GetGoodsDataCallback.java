package com.inappstory.sdk.stories.ui.views;

import java.util.ArrayList;

public interface GetGoodsDataCallback {
    void onSuccess(ArrayList<GoodsItemData> data);
    void onError();
    void onClose();
}
