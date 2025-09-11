package com.inappstory.sdk.banners.ui.banner;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.api.impl.IASSingleStoryImpl;
import com.inappstory.sdk.core.banners.BannerState;
import com.inappstory.sdk.core.banners.IBannerPlaceLoadCallback;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenData;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenStrategy;
import com.inappstory.sdk.inappmessage.domain.stedata.CallToActionData;
import com.inappstory.sdk.inappmessage.domain.stedata.JsSendApiRequestData;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.stories.api.models.ContentId;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.Objects;

public class BannerView extends FrameLayout implements Observer<BannerState> {

    public BannerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    private IBannerViewModel bannerViewModel;
    private BannerWebView bannerWebView;
    private View backgroundView;
    private RelativeLayout loaderContainer;
    private View loader;
    private View refresh;
    private CardView bannerContainer;

    public boolean isLoaded() {
        return isLoaded;
    }

    private boolean isLoaded;
    private Integer bannerId;

    public void viewModel(IBannerViewModel bannerViewModel) {
        this.bannerViewModel = bannerViewModel;
        bannerWebView.slideViewModel(bannerViewModel);
        BannerState state = bannerViewModel.getCurrentBannerState();
        bannerId = state.bannerId();
        Log.e("UpdateBannerState", state.toString());
        onUpdate(state);
        bannerWebView.checkIfClientIsSet();
    }

    public void setSize(float itemWidth, float contentRatio) {
        if (bannerContainer != null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    (int) itemWidth,
                    (int) (itemWidth / contentRatio)
            );
            layoutParams.gravity = Gravity.CENTER;
            bannerContainer.setLayoutParams(
                    layoutParams
            );
            /*bannerContainer.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            (int) size.getWidth(), (int) size.getHeight())
            );*/
        }
    }

    BannerState currentState;

    @SuppressLint("WrongViewCast")
    private void init(Context context) {
        View.inflate(context, R.layout.cs_banner_item, this);
        bannerContainer = findViewById(R.id.bannerContainer);
        bannerContainer.setCardBackgroundColor(Color.TRANSPARENT);
        bannerContainer.setCardElevation(0f);
        bannerContainer.setUseCompatPadding(false);
        loaderContainer = findViewById(R.id.loaderContainer);
        bannerWebView = findViewById(R.id.contentWebView);
        backgroundView = findViewById(R.id.background);
        bannerWebView.setHost(this);
        loaderContainer.addView(createLoader(context));
        loaderContainer.addView(createRefresh(context));
        Log.e("BannerPagerAdapter", "initView");
        bannerWebView.setBackgroundColor(Color.argb(1, 255, 255, 255));
    }

    public void setLoadingPlaceholder(View view) {
        ((ViewGroup) loader).addView(view);
    }

    public void stopBanner() {
        bannerViewModel.singleTimeEvents().unsubscribe(
                steTypeAndDataObserver
        );
        if (!isLoaded) return;
        if (bannerWebView == null) return;
        if (bannerViewModel != null) {
            bannerViewModel.stopSlide();
        }
        bannerWebView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        bannerWebView.stopSlide(false);
                    }
                }
        );
    }

    public void setBannerRadius(float radius) {
        if (bannerContainer == null) return;
        bannerContainer.setRadius(radius);
    }

    public void setBannerBackground(Drawable background) {
        backgroundView.setBackground(background);
    }

    public void resumeBanner() {
        if (!isLoaded) return;
        if (bannerWebView == null) return;
        synchronized (pauseLock) {
            if (!paused) return;
        }
        bannerWebView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        bannerWebView.resumeSlide();
                    }
                }
        );
    }

    private boolean paused = false;
    private final Object pauseLock = new Object();

    public void pauseBanner() {
        if (!isLoaded) return;
        if (bannerWebView == null) return;
        bannerWebView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        synchronized (pauseLock) {
                            paused = true;
                        }
                        bannerWebView.pauseSlide();
                    }
                }
        );
    }

    public void startBanner() {
        bannerViewModel.singleTimeEvents().subscribe(
                steTypeAndDataObserver
        );
        if (!isLoaded) return;
        if (bannerWebView == null) return;
        if (bannerViewModel != null) {
            bannerViewModel.bannerIsShown();
        }
        bannerWebView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.e("SlideLC", "startBanner " + bannerId);
                        bannerWebView.setClientVariables();
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

    Observer<STETypeAndData> steTypeAndDataObserver = new Observer<STETypeAndData>() {
        @Override
        public void onUpdate(final STETypeAndData newValue) {
            if (newValue == null) return;
            InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    switch (newValue.type()) {
                        case AUTO_SLIDE_END:
                            final BannerWebView bannerWebView1 = bannerWebView;
                            if (bannerWebView1 != null)
                                bannerWebView1.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        bannerWebView1.autoSlideEnd();
                                    }
                                });
                            break;
                        case CALL_TO_ACTION:
                            callToActionHandle(
                                    core,
                                    (CallToActionData) newValue.data()
                            );
                            break;
                        case JS_SEND_API_REQUEST:
                            jsSendApiRequestHandle(
                                    core,
                                    (JsSendApiRequestData) newValue.data()
                            );
                            break;
                        case OPEN_STORY:
                            openStoryHandle(
                                    core,
                                    (ContentIdWithIndex) newValue.data()
                            );
                            break;
                        case OPEN_GAME:
                            openGameHandle(
                                    core,
                                    (ContentId) newValue.data()
                            );
                            break;

                    }
                }
            });
        }
    };

    private void openStoryHandle(IASCore core, final ContentIdWithIndex contentIdWithIndex) {
        try {
            if (!(getContext() instanceof Activity)) return;
            AppearanceManager appearanceManager = AppearanceManager.checkOrCreateAppearanceManager(null);
            ((IASSingleStoryImpl) core.singleStoryAPI()).show(
                    (Activity) getContext(),
                    contentIdWithIndex.id() + "",
                    appearanceManager,
                    null,
                    contentIdWithIndex.index(),
                    false,
                    SourceType.SINGLE,
                    ShowStory.ACTION_CUSTOM
            );
        } catch (Exception e) {

        }

    }

    private void openGameHandle(IASCore core, final ContentId contentId) {
        try {
            if (bannerViewModel == null || !(getContext() instanceof Activity)) return;
            core.screensManager().openScreen(
                    (Activity) getContext(),
                    new LaunchGameScreenStrategy(core, false)
                            .data(new LaunchGameScreenData(
                                    null,
                                    bannerViewModel.getCurrentBannerData(),
                                    contentId.id()
                            ))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jsSendApiRequestHandle(
            IASCore core,
            JsSendApiRequestData apiRequestData
    ) {
        new JsApiClient(
                core,
                getContext(),
                core.projectSettingsAPI().host()
        ).sendApiRequest(apiRequestData.data(), new JsApiResponseCallback() {
            @Override
            public void onJsApiResponse(String result, String cb) {
                bannerWebView.loadJsApiResponse(result, cb);
            }
        });
    }


    private void callToActionHandle(
            IASCore core,
            final CallToActionData data
    ) {
        core.callbacksAPI().useCallback(
                IASCallbackType.CALL_TO_ACTION,
                new UseIASCallback<CallToActionCallback>() {
                    @Override
                    public void use(@NonNull CallToActionCallback callback) {
                        callback.callToAction(
                                getContext(),
                                data.contentData(),
                                data.link(),
                                data.clickAction()
                        );
                    }

                    @Override
                    public void onDefault() {
                        defaultUrlClick(data.link());
                    }
                }
        );
    }

    private void defaultUrlClick(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(url));
        try {
            getContext().startActivity(i);
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).overridePendingTransition(R.anim.popup_show, R.anim.empty_animation);
            }
        } catch (Exception e) {

        }
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
        Log.e("ViewIsAttached", toString());
        if (bannerViewModel != null) {
            bannerViewModel.addSubscriber(this);
        }

    }


    public void setListLoadCallback(IBannerPlaceLoadCallback listLoadCallback) {
        this.listLoadCallback = listLoadCallback;
    }


    public void removeListLoadCallback() {
        this.listLoadCallback = null;
    }

    private IBannerPlaceLoadCallback listLoadCallback;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bannerViewModel != null) {
            bannerViewModel.clearJsStatus();
            bannerViewModel.removeSubscriber(this);
        }
    }


    @Override
    public void onUpdate(final BannerState newValue) {
        if (newValue == null) return;
        if (!Objects.equals(newValue.bannerId(), bannerId)) return;
        if (Objects.equals(currentState, newValue)) return;
        BannerState localCurrentState = currentState;
        currentState = newValue;
        if (localCurrentState == null ||
                (newValue.loadState() != localCurrentState.loadState())
        ) {
            switch (newValue.loadState()) {
                case EMPTY:
                    break;
                case LOADING:
                    showLoader();
                    break;
                case FAILED:
                    if (listLoadCallback != null) {
                        listLoadCallback.bannerLoadError(newValue.bannerId(), newValue.bannerIsActive());
                    }
                  //  showRefresh();
                    break;
                case LOADED:
                    Log.e("UpdateBannerState", "Loaded Event " + newValue.bannerId());
                    if (newValue.content() != null &&
                            !newValue.content().isEmpty()) {
                        if (bannerWebView != null) {
                            if (bannerViewModel.bannerIsActive()) {
                                if (newValue.bannerId() == 33) {
                                    Log.e("LoadBannerContent", "Check " + newValue);
                                }
                                bannerWebView.post(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e("LoadBannerContent", "Load " + newValue.bannerId());

                                                bannerWebView.loadSlide(newValue.content());
                                            }
                                        }
                                );
                            } else {
                                bannerWebView.postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e("LoadBannerContent", "Load " + newValue.bannerId());
                                                bannerWebView.loadSlide(newValue.content());
                                            }
                                        }, 130
                                );
                            }
                        }
                    }
                    break;
            }

        }
        if ((localCurrentState == null || !localCurrentState.renderReady()) && newValue.renderReady()) {
            if (bannerWebView != null) {
                bannerWebView.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                Log.e("LoadBannerContent", "SetClientVariables " + newValue.bannerId());
                                bannerWebView.setClientVariables();
                            }
                        }
                );
            }
        }
        if (localCurrentState == null ||
                (newValue.slideJSStatus() != localCurrentState.slideJSStatus())
        ) {
            switch (newValue.slideJSStatus()) {
                case 0:
                    break;
                case 1:
                    if (bannerWebView != null) {
                        isLoaded = true;
                        if (listLoadCallback != null) {
                            listLoadCallback.bannerLoaded(newValue.bannerId(), newValue.bannerIsActive());
                        }
                        bannerWebView.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoaderContainer();
                                        if (bannerViewModel != null && bannerViewModel.bannerIsActive()) {
                                            Log.e("SlideLC", "slideJSStatus " + newValue.bannerId());
                                            bannerViewModel.bannerIsShown();
                                            bannerWebView.startSlide(null);
                                            //   bannerWebView.resumeSlide();
                                        }
                                    }
                                }
                        );
                    }
                    break;
                case -1:
                    break;
            }

        }
    }
}
