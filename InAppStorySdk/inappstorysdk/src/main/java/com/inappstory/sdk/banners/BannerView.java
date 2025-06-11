package com.inappstory.sdk.banners;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.banners.BannerState;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideState;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.utils.Observer;

import java.util.Random;

public class BannerView extends FrameLayout implements Observer<BannerState> {

    public BannerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    private IBannerViewModel bannerViewModel;
    private BannerWebView bannerWebView;
    private FrameLayout container;

    void viewModel(IBannerViewModel bannerViewModel) {
        this.bannerViewModel = bannerViewModel;
        bannerWebView.slideViewModel(bannerViewModel);
        bannerWebView.checkIfClientIsSet();
    }


    BannerState currentState;

    private void init(Context context) {
        View.inflate(context, R.layout.cs_banner_item, this);
        container = findViewById(R.id.bannerContainer);
        bannerWebView = findViewById(R.id.contentWebView);
        Log.e("BannerPagerAdapter", "initView");
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
    public void onUpdate(final BannerState newValue) {
        if (newValue == null) return;
        if (currentState == null ||
                (newValue.loadState() != currentState.loadState())
        ) {
            switch (newValue.loadState()) {
                case EMPTY:
                case LOADING:
                case FAILED:
                    break;
                case LOADED:
                    if (newValue.content() != null &&
                            !newValue.content().isEmpty()) {
                        if (bannerWebView instanceof View) {
                            ((View) bannerWebView).post(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            bannerWebView.loadSlide(newValue.content());
                                        }
                                    }
                            );
                        }
                    }
                    break;
            }

        }
        if (currentState != null &&
                (newValue.slideJSStatus() != currentState.slideJSStatus())
        ) {
            switch (newValue.slideJSStatus()) {
                case 0:
                    break;
                case 1:
                    if (bannerWebView instanceof View) {
                        ((View) bannerWebView).post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        bannerWebView.startSlide(null);
                                        bannerWebView.resumeSlide();
                                    }
                                }
                        );
                    }
                    break;
                case -1:
                    break;
            }

        }
        currentState = newValue;
    }
}
