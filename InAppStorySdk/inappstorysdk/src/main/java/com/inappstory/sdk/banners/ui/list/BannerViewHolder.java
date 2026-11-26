package com.inappstory.sdk.banners.ui.list;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.R;
import com.inappstory.sdk.banners.ui.banner.BannerView;
import com.inappstory.sdk.core.data.IBanner;

import java.util.Locale;

public class BannerViewHolder extends RecyclerView.ViewHolder {
    public BannerView bannerView;
    private final IBanner banner;
    public int index;

    public BannerViewHolder(@NonNull View itemView, IBanner banner) {
        super(itemView);
        this.banner = banner;
        bannerView = itemView.findViewById(R.id.bannerView);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) bannerView.getLayoutParams();
        layoutParams.dimensionRatio = String.format(Locale.US, "%.0f:100",
                banner.bannerAppearance().singleBannerAspectRatio() * 100
        );
    }

    public IBanner banner() {
        return banner;
    }
}
