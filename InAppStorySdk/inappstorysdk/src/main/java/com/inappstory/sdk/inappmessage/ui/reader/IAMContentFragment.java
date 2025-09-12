package com.inappstory.sdk.inappmessage.ui.reader;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.api.impl.IASSingleStoryImpl;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenData;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenStrategy;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideState;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderState;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderUIStates;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderSlideViewModel;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;
import com.inappstory.sdk.inappmessage.domain.stedata.JsSendApiRequestData;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessagePopupAppearance;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.stories.api.models.ContentId;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.inappmessage.domain.stedata.CallToActionData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ContentViewInteractor;
import com.inappstory.sdk.stories.utils.Observer;

public class IAMContentFragment extends Fragment implements Observer<IAMReaderSlideState> {
    ContentViewInteractor contentWebView;

    IIAMReaderSlideViewModel readerSlideViewModel;
    IAMReaderSlideState currentState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.cs_inappmessage_content_layout,
                container,
                false
        );
    }

    @Override
    public void onDestroyView() {
        if (readerSlideViewModel != null) {
            readerSlideViewModel.removeSubscriber(this);
            readerSlideViewModel.singleTimeEvents().unsubscribe(callToActionDataObserver);
        }
        contentWebView.stopSlide(false);
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        isPaused = true;
        readerSlideViewModel.readerIsClosing();
        contentWebView.pauseSlide();
    }

    @Override
    public void onResume() {
        super.onResume();
        readerSlideViewModel.readerIsOpened(!isPaused);
        contentWebView.resumeSlide();
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
                    requireActivity(),
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
                        requireActivity(),
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
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(url));
        try {
            getActivity().startActivity(i);
            getActivity().overridePendingTransition(R.anim.popup_show, R.anim.empty_animation);
        } catch (Exception e) {

        }
    }

    private boolean isPaused = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentWebView = view.findViewById(R.id.webView);
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
                            IAMContentFragment.this
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
                    setWebViewBackground(readerState.appearance);
            }
        });
    }

    private void setWebViewBackground(InAppMessageAppearance appearance) {
        if (appearance != null) {
            int backgroundColor = Color.WHITE;
            if (appearance instanceof InAppMessageFullscreenAppearance) {
                backgroundColor = ColorUtils.parseColorRGBA(
                        ((InAppMessageFullscreenAppearance) appearance)
                                .backgroundColor()
                );
            } else if (appearance instanceof InAppMessagePopupAppearance) {
                backgroundColor = ColorUtils.parseColorRGBA(
                        ((InAppMessagePopupAppearance) appearance)
                                .backgroundColor()
                );
            } else if (appearance instanceof InAppMessageBottomSheetAppearance) {
                backgroundColor = ColorUtils.parseColorRGBA(
                        ((InAppMessageBottomSheetAppearance) appearance)
                                .backgroundColor()
                );
            }
            contentWebView.setBackgroundColor(Color.argb(1, 255, 255, 255));
        }
    }

    @Override
    public void onUpdate(final IAMReaderSlideState newValue) {
        if (newValue == null) return;
        if (currentState == null ||
                (newValue.contentStatus() != currentState.contentStatus())
        ) {
            switch (newValue.contentStatus()) {
                case 0:
                    break;
                case 1:
                    if (newValue.content() != null &&
                            !newValue.content().isEmpty()) {
                        if (contentWebView instanceof View) {
                            ((View) contentWebView).post(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            contentWebView.loadSlide(newValue.content());
                                        }
                                    }
                            );
                        }
                    }
                    break;
                case -1:
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
        currentState = newValue;
    }
}
