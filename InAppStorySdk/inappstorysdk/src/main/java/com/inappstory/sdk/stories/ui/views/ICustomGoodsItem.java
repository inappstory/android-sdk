package com.inappstory.sdk.stories.ui.views;

import android.view.View;

import androidx.annotation.NonNull;

public interface ICustomGoodsItem {
    @NonNull
    View getView();

    void bindView(View view, GoodsItemData data);
}
