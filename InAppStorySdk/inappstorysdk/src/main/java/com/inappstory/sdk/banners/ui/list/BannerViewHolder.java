package com.inappstory.sdk.banners.ui.list;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.R;
import com.inappstory.sdk.banners.ui.banner.BannerView;

public class BannerViewHolder extends RecyclerView.ViewHolder {
    public BannerView bannerView;
    public int index;

    public BannerViewHolder(@NonNull View itemView, float ratio) {
        super(itemView);
        bannerView = itemView.findViewById(R.id.bannerView);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) bannerView.getLayoutParams();
        layoutParams.dimensionRatio = String.format("%.0f:100", ratio * 100);
    }
}
