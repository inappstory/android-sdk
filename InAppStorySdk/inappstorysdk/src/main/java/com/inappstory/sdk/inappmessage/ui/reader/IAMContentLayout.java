package com.inappstory.sdk.inappmessage.ui.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.CancellationTokenImpl;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.api.impl.IASSingleStoryImpl;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenData;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenStrategy;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideState;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderState;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderUIStates;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderSlideViewModel;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;
import com.inappstory.sdk.inappmessage.domain.stedata.CallToActionData;
import com.inappstory.sdk.inappmessage.domain.stedata.JsSendApiRequestData;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.inappmessage.domain.stedata.SlideInCacheData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.stories.api.models.ContentId;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ContentViewInteractor;
import com.inappstory.sdk.stories.utils.Observer;

import java.util.Objects;

public class IAMContentLayout extends FrameLayout implements Observer<IAMReaderSlideState> {
    ContentViewInteractor contentWebView;

    IIAMReaderSlideViewModel readerSlideViewModel;
    IAMReaderSlideState currentState;

    public IAMContentLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public IAMContentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IAMContentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public void init(Context context) {
        inflate(context, R.layout.cs_inappmessage_content_layout, this);
        contentWebView = findViewById(R.id.webView);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                IIAMReaderViewModel readerViewModel = core.screensManager().iamReaderViewModel();
                IAMReaderState readerState = readerViewModel.getCurrentState();
                readerSlideViewModel = readerViewModel.slideViewModel();
                if (readerSlideViewModel != null) {
                    contentWebView.slideViewModel(readerSlideViewModel);
                    contentWebView.checkIfClientIsSet();
                    readerSlideViewModel.addSubscriber(
                            IAMContentLayout.this
                    );
                    readerSlideViewModel.singleTimeEvents().subscribe(
                            callToActionDataObserver
                    );
                    if (!readerSlideViewModel.loadContent()) {
                        readerViewModel.updateCurrentUiState(IAMReaderUIStates.CLOSED);
                        return;
                    }
                }
                if (readerState != null)
                    setWebViewBackground();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (readerSlideViewModel != null) {
            readerSlideViewModel.removeSubscriber(this);
            readerSlideViewModel.singleTimeEvents().unsubscribe(callToActionDataObserver);
        }
        contentWebView.stopSlide(false);
    }


    public void onPause() {
        isPaused = true;
        if (readerSlideViewModel != null)
            readerSlideViewModel.readerIsClosing();
        contentWebView.pauseSlide();
    }

    public void onResume() {
        if (readerSlideViewModel != null)
            readerSlideViewModel.readerIsOpened(!isPaused);
        contentWebView.resumeSlide();
    }

    public void refreshClick() {
        readerSlideViewModel.reloadContent();
        /*if (!(contentWebView instanceof IAMWebView)) return;
        final IAMWebView localWebView = (IAMWebView) contentWebView;
        IAMReaderSlideState current = currentState;
        if (current == null) return;
        localWebView.showSlides(
                current.slides(),
                JsonParser.mapToJsonString(current.cardAppearance()),
                current.slideIndex()
        );*/
    }

    Observer<STETypeAndData> callToActionDataObserver = new Observer<STETypeAndData>() {
        @Override
        public void onUpdate(final STETypeAndData newValue) {
            if (newValue == null) return;
            InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    switch (newValue.type()) {
                        case CALL_TO_ACTION:
                            callToActionHandle(
                                    core,
                                    (CallToActionData) newValue.data()
                            );
                            break;
                        case SLIDE_IN_CACHE:
                            slideInCache(
                                    (SlideInCacheData) newValue.data()
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
            AppearanceManager appearanceManager = AppearanceManager.checkOrCreateAppearanceManager(null);
            ((IASSingleStoryImpl) core.singleStoryAPI()).show(
                    new CancellationTokenImpl(),
                    getContext(),
                    contentIdWithIndex.id() + "",
                    appearanceManager,
                    null,
                    contentIdWithIndex.index(),
                    true,
                    SourceType.SINGLE,
                    ShowStory.ACTION_CUSTOM
            );
        } catch (Exception e) {

        }

    }

    private void openGameHandle(IASCore core, final ContentId contentId) {
        try {
            IIAMReaderViewModel readerViewModel = core.screensManager().iamReaderViewModel();
            if (core.gamesAPI().gameCanBeOpened(contentId.id())) {
                core.screensManager().openScreen(
                        getContext(),
                        new LaunchGameScreenStrategy(core, true)
                                .data(new LaunchGameScreenData(
                                        null,
                                        readerViewModel.getCurrentInAppMessageData(),
                                        contentId.id()
                                ))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void slideInCache(final SlideInCacheData slideInCacheData) {
        final IAMWebView webView = (IAMWebView) contentWebView;
        webView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    webView.slideInCache(JsonParser.getJson(slideInCacheData));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                contentWebView.loadJsApiResponse(result, cb);
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
        Context context = getContext();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(url));
        try {
            context.startActivity(i);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.popup_show, R.anim.empty_animation);
            }
        } catch (Exception e) {

        }
    }

    private boolean isPaused = false;


    private void setWebViewBackground() {
        contentWebView.setBackgroundColor(Color.argb(1, 255, 255, 255));
    }

    @Override
    public void onUpdate(final IAMReaderSlideState newValue) {
        if (newValue == null) return;
        if (Objects.equals(currentState, newValue)) return;
        IAMReaderSlideState localCurrentState = currentState;
        currentState = newValue;
        if (!(contentWebView instanceof IAMWebView)) return;
        final IAMWebView localWebView = (IAMWebView) contentWebView;
        if (localCurrentState == null ||
                (newValue.contentStatus() != localCurrentState.contentStatus())
        ) {
            switch (newValue.contentStatus()) {
                case 0:
                    break;
                case 1:
                    if (newValue.layout() != null &&
                            !newValue.layout().isEmpty()) {
                        localWebView.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        localWebView.loadSlide(newValue.layout());
                                    }
                                }
                        );
                    }
                    break;
                case 2:
                    localWebView.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    localWebView.setClientVariables();
                                    localWebView.showSlides(
                                            newValue.slides(),
                                            JsonParser.mapToJsonString(newValue.cardAppearance()),
                                            0
                                    );
                                }
                            }
                    );
                    break;
            }

        }
        if (localCurrentState != null &&
                (newValue.slideJSStatus() != localCurrentState.slideJSStatus())
        ) {
            switch (newValue.slideJSStatus()) {
                case 0:
                    break;
                case 1:
                    if (contentWebView instanceof View) {
                        ((View) contentWebView).post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        contentWebView.startSlide(null);
                                        contentWebView.resumeSlide();
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
