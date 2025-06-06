package com.inappstory.sdk.banners;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.banners.BannerState;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.utils.Observer;

public class BannerView extends FrameLayout implements Observer<BannerState> {


    public void setSizes(
            float itemWidth,
            int maxHeight,
            float bannerRatio
    ) {

    }

    public BannerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    private IBannerViewModel bannerViewModel;
    private IASWebView bannerWebView;
    private FrameLayout container;

    void viewModel(IBannerViewModel bannerViewModel) {
        this.bannerViewModel = bannerViewModel;
    }

    private void init(Context context) {
        View.inflate(context, R.layout.cs_banner_item, this);
        container = findViewById(R.id.bannerContainer);
        bannerWebView = findViewById(R.id.contentWebView);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (bannerViewModel != null) bannerViewModel.addSubscriber(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bannerViewModel != null) bannerViewModel.removeSubscriber(this);
    }

    @Override
    public void onUpdate(BannerState newValue) {

    }
}
