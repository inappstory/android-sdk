package com.inappstory.sdk.inappmessage.ui.reader;

import static com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer.CONTAINER_ID;
import static com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer.CONTENT_ID;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.CancellationTokenWithStatus;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.BaseIAMScreen;
import com.inappstory.sdk.inappmessage.InAppMessageViewController;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderLoadStates;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderLoaderStates;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderState;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderUIStates;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessagePopupAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageToastAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageUndefinedAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageUndefinedSettings;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContainerCallback;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.inappmessage.ui.widgets.impl.BottomSheetContentContainer;
import com.inappstory.sdk.inappmessage.ui.widgets.impl.BottomSheetSlideCallback;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

import java.util.Objects;

public class InAppMessageMainFragment extends Fragment implements Observer<IAMReaderState>, BaseIAMScreen {
    private IAMReaderLoadStates currentLoadState = IAMReaderLoadStates.EMPTY;
    private IAMReaderLoaderStates currentLoaderState = IAMReaderLoaderStates.EMPTY;
    private IAMReaderUIStates currentUIState = IAMReaderUIStates.CLOSED;
    private IIAMReaderViewModel readerViewModel;
    private boolean contentIsPreloaded;
    private InAppMessageAppearance appearance = new InAppMessageUndefinedSettings();
    private IAMContentContainer contentContainer;
    InAppMessageViewController controller;

    public void setController(InAppMessageViewController controller) {
        this.controller = controller;
    }

    @Override
    public void onDestroyView() {
        IIAMReaderViewModel viewModel = readerViewModel;
        if (contentContainer != null) {
            contentContainer.uiContainerCallback(null);
        }
        if (viewModel != null) {
            if (contentContainer instanceof BottomSheetContentContainer) {
                viewModel.slideViewModel().removeScrollSubscriber(
                        (BottomSheetContentContainer) contentContainer
                );
            }
            if (viewModel.controller() != null)
                viewModel.controller().unsubscribeView(this);
            viewModel.onCloseAction();
            viewModel.removeSubscriber(InAppMessageMainFragment.this);
            viewModel.clear();
        } else {
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.screensManager().iamReaderViewModel()
                            .removeSubscriber(InAppMessageMainFragment.this);
                }
            });
        }
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager().getIAMScreenHolder()
                        .unsubscribeScreen(InAppMessageMainFragment.this);
            }
        });
        super.onDestroyView();
    }

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
                if (readerViewModel.controller() != null)
                    readerViewModel.controller().subscribeView(
                            InAppMessageMainFragment.this
                    );
                IAMReaderState state = readerViewModel.getCurrentState();
                contentIsPreloaded = state.contentIsPreloaded;
                appearance = state.appearance;
            }
        });
        View v;
        if (appearance instanceof InAppMessagePopupAppearance) {
            v = inflater.inflate(
                    R.layout.cs_inappmessage_popup_layout,
                    container,
                    false
            );
        } else if (appearance instanceof InAppMessageFullscreenAppearance) {
            v = inflater.inflate(
                    R.layout.cs_inappmessage_fullscreen_layout,
                    container,
                    false
            );
        } else if (appearance instanceof InAppMessageToastAppearance) {
            v = inflater.inflate(
                    R.layout.cs_inappmessage_toast_layout,
                    container,
                    false
            );
        } else if (appearance instanceof InAppMessageBottomSheetAppearance) {
            v = inflater.inflate(
                    R.layout.cs_inappmessage_bottomsheet_layout,
                    container,
                    false
            );
        } else {
            v = inflater.inflate(
                    R.layout.cs_inappmessage_fullscreen_layout,
                    container,
                    false
            );
        }
        contentContainer = v.findViewById(CONTAINER_ID);
        contentContainer.setVisibility(View.INVISIBLE);
        if (appearance != null && !(appearance instanceof InAppMessageUndefinedAppearance)) {
            contentContainer.appearance(appearance);
        }
        contentContainer.uiContainerCallback(containerCallback);
        return v;
    }

    IAMContainerCallback containerCallback = new IAMContainerCallback() {
        @Override
        public void countSafeArea(Pair<Integer, Integer> safeArea) {
            readerViewModel.updateCurrentSafeArea(safeArea);
        }

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
        if (!contentIsPreloaded)
            contentContainer.showLoader();
        contentContainer.showWithAnimation();
    }


    public void hideContainerWithAnimation() {
        contentContainer.closeWithAnimation();
    }

    public void hideContainer() {
        if (contentContainer != null) {

        }
        try {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction t = fragmentManager.beginTransaction()
                    .remove(this);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    IAMContentFragment contentFragment;

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        if (readerViewModel == null) return;
        if (controller != null) {
            controller.subscribeView(this);
        }
        if (!contentIsPreloaded) {
            readerViewModel.updateCurrentUiState(IAMReaderUIStates.OPENING);
            readerViewModel.updateCurrentLoaderState(IAMReaderLoaderStates.LOADING);
        }
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager()
                        .getIAMScreenHolder().subscribeScreen(InAppMessageMainFragment.this);
            }
        });
        contentFragment = new IAMContentFragment();
        FragmentTransaction t = getChildFragmentManager().beginTransaction()
                .add(
                        CONTENT_ID,
                        contentFragment,
                        "IAM_CONTENT_FRAGMENT"
                );
        t.addToBackStack("IAM_CONTENT_FRAGMENT");
        t.commit();
        if (contentContainer != null) {
            contentContainer.setRefreshClick(v -> {
                if (contentFragment != null) {
                    contentFragment.refreshClick();
                }
            });
            if (contentContainer instanceof BottomSheetContentContainer) {
                ((BottomSheetContentContainer) contentContainer).slideCallback(new BottomSheetSlideCallback() {
                    @Override
                    public void onSlide(float slideOffset) {
                        Log.e("contentFragmentOffset", "" + slideOffset);
                        if (contentFragment != null) {
                            contentFragment.changeContentWebViewSwipe(slideOffset < 1 && slideOffset > 0);
                        }
                    }
                });
                readerViewModel.slideViewModel().addScrollSubscriber(
                        (BottomSheetContentContainer) contentContainer
                );
            }
        }
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                String tokenId = readerViewModel.getCurrentState().cancellationTokenUID;
                if (tokenId != null) {
                    CancellationTokenWithStatus token = core.cancellationTokenPool().getTokenByUID(tokenId);
                    if (token != null) {
                        if (token.cancelled())
                            forceFinish();
                        else
                            token.disable();
                    }
                }
            }
        });
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
                if (newValue.loaderState != currentLoaderState) {
                    loaderStateChanged(newValue.loaderState);
                }
            }
        });

    }

    private void loaderStateChanged(IAMReaderLoaderStates state) {
        currentLoaderState = state;
        switch (state) {
            case EMPTY:
                break;
            case FAILED:
                contentContainer.showRefresh();
                break;
            case LOADED:
                contentContainer.hideLoader();
                contentContainer.hideRefresh();
                break;
            case LOADING:
                contentContainer.showLoader();
                break;
        }
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
                if (readerViewModel != null) readerViewModel.onOpenAction();
                break;
        }
    }


    private void loadStateIsChanged(IAMReaderLoadStates newState) {
        if (Objects.equals(currentLoadState, newState)) return;
        currentLoadState = newState;
        if (newState == null) return;
        switch (newState) {
            case ASSETS_LOADED:
            case CONTENT_LOADED:
                contentFragment.readerSlideViewModel.updateLayout();
                break;
            case ASSETS_FAILED:
            case CONTENT_FAILED:
            case CONTENT_LOADING:
            case ASSETS_LOADING:
                break;
        }
        if (Objects.requireNonNull(newState) == IAMReaderLoadStates.RENDER_READY) {
            if (currentUIState != IAMReaderUIStates.OPENED &&
                    currentUIState != IAMReaderUIStates.OPENING) {
                readerViewModel.updateCurrentUiState(IAMReaderUIStates.OPENING);
            }
        } else if (Objects.requireNonNull(newState) == IAMReaderLoadStates.CONTENT_FAILED_CLOSE) {
            readerViewModel.updateCurrentUiState(IAMReaderUIStates.CLOSING);
        }
    }

    @Override
    public void forceFinish() {
        contentContainer.closeWithoutAnimation();
    }

    @Override
    public void close() {
        contentContainer.closeWithAnimation();
    }

    @Override
    public void pauseScreen() {
        if (contentFragment != null) {
            contentFragment.pauseScreen();
        }
    }

    @Override
    public void resumeScreen() {
        if (contentFragment != null) {
            contentFragment.resumeScreen();
        }
    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {

    }

    @Override
    public void permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onPause() {
        //pauseScreen();
        super.onPause();
    }

    @Override
    public void onResume() {
        //resumeScreen();
        super.onResume();
    }

    @Override
    public FragmentManager getScreenFragmentManager() {
        return getParentFragmentManager();
    }
}
