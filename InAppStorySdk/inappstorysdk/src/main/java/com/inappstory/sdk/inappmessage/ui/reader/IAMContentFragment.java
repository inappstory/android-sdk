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

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideState;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderSlideViewModel;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageModalAppearance;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.callbackdata.CallToActionData;
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
            readerSlideViewModel.callToActionDataSTE().unsubscribe(callToActionDataObserver);
        }
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        contentWebView.pauseSlide();
    }

    @Override
    public void onResume() {
        super.onResume();
        contentWebView.resumeSlide();
    }

    Observer<CallToActionData> callToActionDataObserver = new Observer<CallToActionData>() {
        @Override
        public void onUpdate(final CallToActionData newValue) {
            if (newValue == null) return;
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.callbacksAPI().useCallback(
                            IASCallbackType.CALL_TO_ACTION,
                            new UseIASCallback<CallToActionCallback>() {
                                @Override
                                public void use(@NonNull CallToActionCallback callback) {
                                    callback.callToAction(
                                            getContext(),
                                            newValue.slideData(),
                                            newValue.link(),
                                            newValue.clickAction()
                                    );
                                }

                                @Override
                                public void onDefault() {
                                    defaultUrlClick(newValue.link());
                                }
                            }
                    );
                }
            });

        }
    };

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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentWebView = view.findViewById(R.id.webView);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                IIAMReaderViewModel readerViewModel = core.screensManager().iamReaderViewModel();
                readerSlideViewModel = readerViewModel.slideViewModel();
                if (readerSlideViewModel != null) {
                    contentWebView.slideViewModel(readerSlideViewModel);
                    contentWebView.checkIfClientIsSet();
                    readerSlideViewModel.addSubscriber(
                            IAMContentFragment.this
                    );
                    readerSlideViewModel.loadContent();
                    readerSlideViewModel.callToActionDataSTE().subscribe(
                            callToActionDataObserver
                    );
                }
                setWebViewBackground(readerViewModel.getCurrentState().appearance);
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
            } else if (appearance instanceof InAppMessageModalAppearance) {
                backgroundColor = ColorUtils.parseColorRGBA(
                        ((InAppMessageModalAppearance) appearance)
                                .backgroundColor()
                );
            } else if (appearance instanceof InAppMessageBottomSheetAppearance) {
                backgroundColor = ColorUtils.parseColorRGBA(
                        ((InAppMessageBottomSheetAppearance) appearance)
                                .backgroundColor()
                );
            }
            contentWebView.setBackgroundColor(backgroundColor);
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
        currentState = newValue;
    }
}
