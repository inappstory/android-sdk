package com.inappstory.sdk.banners.ui.list;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.R;
import com.inappstory.sdk.banners.ui.banner.BannerView;

public class BannerViewHolder extends RecyclerView.ViewHolder {
    public BannerView bannerView;

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);
        bannerView = itemView.findViewById(R.id.bannerView);
    }
}
