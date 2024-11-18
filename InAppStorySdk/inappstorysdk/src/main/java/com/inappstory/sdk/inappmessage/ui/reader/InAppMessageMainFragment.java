package com.inappstory.sdk.inappmessage.ui.reader;

import static com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer.CONTAINER_ID;
import static com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer.CONTENT_ID;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderLoadStates;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideState;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderState;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderUIStates;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageModalAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageBottomSheetSettings;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContainerCallback;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.stories.utils.Observer;

import java.util.Objects;

public class InAppMessageMainFragment extends Fragment implements Observer<IAMReaderState> {
    private IAMReaderLoadStates currentLoadState = IAMReaderLoadStates.EMPTY;
    private IAMReaderUIStates currentUIState = IAMReaderUIStates.CLOSED;
    private IIAMReaderViewModel readerViewModel;
    private boolean showOnlyIfLoaded;
    private InAppMessageAppearance appearance = new InAppMessageBottomSheetSettings();
    private IAMContentContainer contentContainer;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                readerViewModel = core.screensManager().iamReaderViewModel();
                readerViewModel.addSubscriber(InAppMessageMainFragment.this);
                IAMReaderState state = readerViewModel.getCurrentState();
                showOnlyIfLoaded = state.showOnlyIfLoaded;
                appearance = state.appearance;
            }
        });
        View v;
        if (appearance instanceof InAppMessageModalAppearance) {
            v = inflater.inflate(
                    R.layout.cs_inappmessage_modal_layout,
                    container,
                    false
            );
        } else if (appearance instanceof InAppMessageFullscreenAppearance) {
            v = inflater.inflate(
                    R.layout.cs_inappmessage_fullscreen_layout,
                    container,
                    false
            );
        } else {
            v = inflater.inflate(
                    R.layout.cs_inappmessage_bottomsheet_layout,
                    container,
                    false
            );
        }
        contentContainer = v.findViewById(CONTAINER_ID);
        return v;
    }

    IAMContainerCallback containerCallback = new IAMContainerCallback() {
        @Override
        public void onShown() {
            readerViewModel.updateCurrentUiState(IAMReaderUIStates.OPENED);
        }

        @Override
        public void onClosed() {
            readerViewModel.updateCurrentUiState(IAMReaderUIStates.CLOSED);
        }
    };

    public void showContainer() {
        contentContainer.showWithAnimation(containerCallback);
    }

    public void hideContainerWithAnimation() {
        contentContainer.closeWithAnimation(containerCallback);
    }

    public void hideContainer() {
        contentContainer.closeWithoutAnimation(containerCallback);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        if (readerViewModel == null) return;
        if (!showOnlyIfLoaded) {
            readerViewModel.updateCurrentUiState(IAMReaderUIStates.OPENING);
        }
        IAMContentFragment contentFragment = new IAMContentFragment();
        FragmentTransaction t = getChildFragmentManager().beginTransaction()
                .add(
                        CONTENT_ID,
                        contentFragment,
                        "IAM_CONTENT_FRAGMENT"
                );
        t.addToBackStack("IAM_CONTENT_FRAGMENT");
        t.commit();
    }

    @Override
    public void onUpdate(final IAMReaderState newValue) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (newValue.uiState != currentUIState) {
                    uiStateIsChanged(newValue.uiState);
                }
                if (newValue.loadState != currentLoadState) {
                    loadStateIsChanged(newValue.loadState);
                }
            }
        });

    }

    private void uiStateIsChanged(IAMReaderUIStates uiState) {
        currentUIState = uiState;
        switch (uiState) {
            case OPENING:
                showContainer();
                break;
            case CLOSING:
                hideContainerWithAnimation();
                break;
            case CLOSED:
                hideContainer();
                break;
            case OPENED:
                break;
        }
    }

    private void loadStateIsChanged(IAMReaderLoadStates newState) {
        currentLoadState = newState;
        if (Objects.requireNonNull(newState) == IAMReaderLoadStates.LOADED) {
            if (currentUIState != IAMReaderUIStates.OPENED &&
                    currentUIState != IAMReaderUIStates.OPENING) {
                readerViewModel.updateCurrentUiState(IAMReaderUIStates.OPENING);
            }
        }
    }
}
