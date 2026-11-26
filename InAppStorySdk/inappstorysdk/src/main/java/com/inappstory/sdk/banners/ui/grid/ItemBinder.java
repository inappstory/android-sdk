package com.inappstory.sdk.banners.ui.grid;

import android.view.View;
import android.view.ViewGroup;

public interface ItemBinder<T> {
    View createView(ViewGroup parent, int position);
    void bindView(View view, T item, int position);
}