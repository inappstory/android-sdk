package com.inappstory.sdk.stories.ui.views.goodswidget;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public interface ICustomGoodsItem {
    @NonNull
    View getView(Context context);

    void bindView(View view, GoodsItemData data);
}
