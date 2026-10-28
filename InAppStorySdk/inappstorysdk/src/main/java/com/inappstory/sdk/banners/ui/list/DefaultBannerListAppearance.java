package com.inappstory.sdk.banners.ui.list;


import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.core.banners.ICustomBannerListAppearance;

public class DefaultBannerListAppearance implements ICustomBannerListAppearance {


    @Override
    public int edgeBannersPadding() {
        return 4;
    }

    @Override
    public int bannersGap() {
        return 8;
    }

    @Override
    public int cornerRadius() {
        return 16;
    }

    @Override
    public final int columnCount() {
        return 1;
    }

    @Override
    public int orientation() {
        return RecyclerView.VERTICAL;
    }

    @Override
    public View loadingPlaceholder(Context context) {
        return null;
    }
}
