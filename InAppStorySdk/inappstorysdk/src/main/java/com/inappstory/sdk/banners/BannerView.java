package com.inappstory.sdk.banners;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.banners.BannerState;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideState;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.Random;

public class BannerView extends FrameLayout implements Observer<BannerState> {

    public BannerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    private IBannerViewModel bannerViewModel;
    private BannerWebView bannerWebView;
    private FrameLayout container;
    private RelativeLayout loaderContainer;
    private View loader;
    private View refresh;

    public boolean isLoaded() {
        return isLoaded;
    }

    private boolean isLoaded;

    void viewModel(IBannerViewModel bannerViewModel) {
        this.bannerViewModel = bannerViewModel;
        bannerWebView.slideViewModel(bannerViewModel);
        bannerWebView.checkIfClientIsSet();
    }


    BannerState currentState;

    @SuppressLint("WrongViewCast")
    private void init(Context context) {
        View.inflate(context, R.layout.cs_banner_item, this);
        container = findViewById(R.id.bannerContainer);
        loaderContainer = findViewById(R.id.loaderContainer);
        bannerWebView = findViewById(R.id.contentWebView);
        bannerWebView.setHost(this);
        loaderContainer.addView(createLoader(context));
        loaderContainer.addView(createRefresh(context));
        Log.e("BannerPagerAdapter", "initView");
    }

    void stopBanner() {
        if (!isLoaded) return;
        if (bannerWebView == null) return;
        bannerWebView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        bannerWebView.stopSlide();
                    }
                }
        );
    }

    void resumeBanner() {
        if (!isLoaded) return;
        if (bannerWebView == null) return;
        bannerWebView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        bannerWebView.resumeSlide();
                    }
                }
        );
    }

    void pauseBanner() {
        if (!isLoaded) return;
        if (bannerWebView == null) return;
        bannerWebView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        bannerWebView.pauseSlide();
                    }
                }
        );
    }

    void startBanner() {
        if (!isLoaded) return;
        if (bannerWebView == null) return;
        bannerWebView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        bannerWebView.startSlide(null);
                    }
                }
        );
    }

    private View createLoader(Context context) {
        loader = new FrameLayout(context);
        loader.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT,
                MATCH_PARENT));
        loader.setElevation(8);
        ((ViewGroup) loader).addView(AppearanceManager.getLoader(context, Color.WHITE));
        return loader;
    }

    private View createRefresh(Context context) {
        refresh = new ImageView(context);
        RelativeLayout.LayoutParams refreshLp = new RelativeLayout.LayoutParams(
                Sizes.dpToPxExt(40, getContext()),
                Sizes.dpToPxExt(40, getContext())
        );
        refreshLp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        refresh.setElevation(18);
        ((ImageView) refresh).setScaleType(ImageView.ScaleType.FIT_XY);
        refresh.setVisibility(View.GONE);
        ((ImageView) refresh).setImageDrawable(context.getResources().getDrawable(
                AppearanceManager.getCommonInstance().csRefreshIcon()
        ));
        refresh.setLayoutParams(refreshLp);
        return refresh;
    }

    private void showRefresh() {
        loaderContainer.post(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(GONE);
                refresh.setVisibility(VISIBLE);
                loaderContainer.setVisibility(VISIBLE);
            }
        });
    }

    private void showLoader() {
        loaderContainer.post(new Runnable() {
            @Override
            public void run() {
                refresh.setVisibility(GONE);
                loader.setVisibility(VISIBLE);
                loaderContainer.setVisibility(VISIBLE);
            }
        });
    }

    private void hideLoaderContainer() {
        loaderContainer.post(new Runnable() {
            @Override
            public void run() {
                loaderContainer.setVisibility(GONE);
                loader.setVisibility(GONE);
                refresh.setVisibility(GONE);
            }
        });
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
                    break;
                case LOADING:
                    showLoader();
                    break;
                case FAILED:
                    showRefresh();
                    break;
                case LOADED:
                    if (newValue.content() != null &&
                            !newValue.content().isEmpty()) {
                        if (bannerWebView instanceof View) {
                            ((View) bannerWebView).post(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            hideLoaderContainer();
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
                        isLoaded = true;
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
