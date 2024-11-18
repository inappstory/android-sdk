package com.inappstory.sdk.inappmessage.ui.reader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideState;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderSlideViewModel;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ContentViewInteractor;
import com.inappstory.sdk.stories.utils.Observable;
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
        if (readerSlideViewModel != null)
            readerSlideViewModel.removeSubscriber(this);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentWebView = view.findViewById(R.id.webView);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                readerSlideViewModel = core.screensManager()
                        .iamReaderViewModel().slideViewModel();
                if (readerSlideViewModel != null) {
                    contentWebView.slideViewModel(readerSlideViewModel);
                    contentWebView.checkIfClientIsSet();
                    readerSlideViewModel.addSubscriber(
                            IAMContentFragment.this
                    );
                    readerSlideViewModel.loadContent();
                }
            }
        });

    }

    @Override
    public void onUpdate(final IAMReaderSlideState newValue) {
        if (newValue == null) return;
        if (
                currentState == null ||
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
